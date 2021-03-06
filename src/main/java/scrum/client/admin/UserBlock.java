package scrum.client.admin;

import ilarkesto.core.scope.Scope;
import ilarkesto.gwt.client.AOutputViewEditWidget;
import ilarkesto.gwt.client.TableBuilder;
import scrum.client.ScrumGwt;
import scrum.client.common.ABlockWidget;
import scrum.client.common.BlockHeaderWidget;
import scrum.client.common.BlockWidgetFactory;
import scrum.client.img.Img;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class UserBlock extends ABlockWidget<User> {

	private Anchor lastLoginLabel;
	private SimplePanel iconWrapper;

	@Override
	protected void onInitializationHeader(BlockHeaderWidget header) {
		User user = getObject();
		iconWrapper = header.insertPrefixIcon();
		lastLoginLabel = header.appendCenterSuffix("");
		header.addMenuAction(new ResetUserPasswordAction(user));
		header.addMenuAction(new ConfirmUserEmailAction(user));
		header.addMenuAction(new DisableUserAction(user));
		header.addMenuAction(new EnableUserAction(user));
		header.addMenuAction(new DeleteUserAction(user));
	}

	@Override
	protected void onUpdateHeader(BlockHeaderWidget header) {
		User user = getObject();
		header.setDragHandle("usr");
		header.setCenter(user.getName());
		lastLoginLabel.setText(ScrumGwt.getPeriodToAsShortestString("login ", user.getLastLoginDateAndTime(), " ago"));
		Image icon = null;
		if (user.isDisabled()) {
			icon = Img.bundle.usrDisabled().createImage();
			icon.setTitle("User is disabled.");
		} else if (!user.isEmailVerified()) {
			icon = Img.bundle.usrEmailUnverified().createImage();
			icon.setTitle("Users email is not verified.");
		}
		iconWrapper.setWidget(icon);
	}

	@Override
	protected Widget onExtendedInitialization() {
		final User user = getObject();
		TableBuilder tb = ScrumGwt.createFieldTable();
		tb.addFieldRow("Name", user.getNameModel());
		tb.addFieldRow("E-Mail", user.getEmailModel());
		if (!Scope.get().getComponent(SystemConfig.class).isOpenIdDisabled()) {
			tb.addFieldRow("OpenID", user.getOpenIdModel());
		}
		tb.addFieldRow("Projects", new AOutputViewEditWidget() {

			@Override
			protected void onViewerUpdate() {
				setViewer(ScrumGwt.createToHtmlItemsWidget(user.getProjects()));
			}
		});
		return tb.createTable();
	}

	public static final BlockWidgetFactory<User> FACTORY = new BlockWidgetFactory<User>() {

		@Override
		public UserBlock createBlock() {
			return new UserBlock();
		}
	};
}
