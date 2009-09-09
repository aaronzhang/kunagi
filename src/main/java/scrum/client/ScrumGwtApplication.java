package scrum.client;

import ilarkesto.gwt.client.ADataTransferObject;
import ilarkesto.gwt.client.AGwtEntity;
import ilarkesto.gwt.client.GwtLogger;

import java.util.LinkedList;

import scrum.client.admin.User;
import scrum.client.collaboration.ChatMessage;
import scrum.client.collaboration.ChatWidget;
import scrum.client.dnd.DndManager;
import scrum.client.project.Project;
import scrum.client.workspace.Ui;
import scrum.client.workspace.WorkareaWidget;
import scrum.client.workspace.WorkspaceWidget;

import com.google.gwt.user.client.ui.RootPanel;

public class ScrumGwtApplication extends GScrumGwtApplication {

	private Dao dao = new Dao();

	private User user;
	private Project project;
	private LinkedList<ChatMessage> chatMessages;
	private DndManager dndManager;
	private Ui ui;
	private boolean developmentMode;
	private long lastDataReceiveTime = System.currentTimeMillis();
	private PingTimer pingTimer;

	/**
	 * Application entry point.
	 */
	public void onModuleLoad() {

		// workaround for GWT issue 1813
		// http://code.google.com/p/google-web-toolkit/issues/detail?id=1813
		RootPanel.get().getElement().getStyle().setProperty("position", "relative");

		ui = Ui.get();
		ui.update();
		RootPanel.get("workspace").add(ui);
		ui.lock("Loading...");
		callPing(new Runnable() {

			public void run() {
				ui.showLogin();
			}
		});

		pingTimer = new PingTimer();
		pingTimer.scheduleRepeating(15000);
	}

	public ChatMessage postSystemMessage(String text, boolean distribute) {
		return postMessage(null, text, distribute);
	}

	public ChatMessage postMessage(String text) {
		return postMessage(user, text, true);
	}

	private ChatMessage postMessage(User author, String text, boolean distribute) {
		ChatMessage msg = new ChatMessage(getProject(), author, text);
		addChatMessage(msg);
		if (distribute) getDao().createChatMessage(msg);
		return msg;
	}

	public LinkedList<ChatMessage> getChatMessages() {
		return chatMessages;
	}

	void addChatMessage(ChatMessage msg) {
		if (project == null || !msg.isProject(project)) return;
		if (chatMessages.contains(msg)) return;
		chatMessages.add(msg);
		if (chatMessages.size() > 50) chatMessages.remove(0);
		ChatWidget.get().update();
	}

	public User getUser() {
		return user;
	}

	public Ui getUi() {
		return ui;
	}

	public boolean isDevelopmentMode() {
		return developmentMode;
	}

	public long getLastDataReceiveTime() {
		return lastDataReceiveTime;
	}

	@Override
	protected void handleDataFromServer(ADataTransferObject adata) {
		super.handleDataFromServer(adata);

		DataTransferObject data = (DataTransferObject) adata;

		if (data.containsEntities()) {
			lastDataReceiveTime = System.currentTimeMillis();
			pingTimer.schedule();
		}

		if (data.entityIdBase != null) {
			getDao().setEntityIdBase(data.entityIdBase);
			GwtLogger.DEBUG("entityIdBase:", data.entityIdBase);
		}

		if (data.developmentMode != null) {
			developmentMode = data.developmentMode;
		}

		if (data.isUserSet()) {
			user = getDao().getUser(data.getUserId());
		}

	}

	@Override
	protected void handleCommunicationError(Throwable ex) {
		ex.printStackTrace();
		ui.lock("Error: " + ex.getMessage());
	}

	public void openProject(Project project) {
		Ui.get().lock("Loading project...");
		this.project = project;
		this.chatMessages = new LinkedList<ChatMessage>();
		callSelectProject(project.getId(), new Runnable() {

			public void run() {
				Ui.get().unlock();
				Ui.get().showWorkspace();
				WorkareaWidget.get().showProjectOverview();
			}
		});

		postSystemMessage(user.getName() + " logged in.", true);
	}

	public void closeProject() {
		callCloseProject();
		this.project = null;
		Dao dao = getDao();
		dao.clearChatMessages();
		dao.clearImpediments();
		dao.clearQualitys();
		dao.clearRequirements();
		dao.clearRisks();
		dao.clearSprints();
		dao.clearTasks();
		Ui.get().showStartPage();
	}

	public void showEntityByReference(final String reference) {
		AGwtEntity entity = getDao().getEntityByReference(reference);
		if (entity != null) {
			WorkareaWidget.get().showEntity(entity);
			return;
		}
		callRequestEntityByReference(reference, new Runnable() {

			public void run() {
				AGwtEntity entity = getDao().getEntityByReference(reference);
				if (entity == null) {
					postSystemMessage("Object does not exist: " + reference, false);
					return;
				}
				WorkareaWidget.get().showEntity(entity);
			}
		});
	}

	public Project getProject() {
		return project;
	}

	@Override
	public Dao getDao() {
		return dao;
	}

	public static ScrumGwtApplication get() {
		return (ScrumGwtApplication) singleton;
	}

	public DndManager getDndManager() {
		if (dndManager == null) {
			dndManager = new DndManager();
		}
		return dndManager;
	}

	public void logout() {
		Ui.get().lock("logging out");
		ScrumGwtApplication.get().callLogout();
		closeProject();
		user = null;
		getDao().clearAllEntities();
		Ui.get().showLogin();
	}

	// --- helper ---

	public WorkspaceWidget getWorkspaceWidget() {
		return getUi().getWorkspace();
	}

}
