package scrum.client.project;

import ilarkesto.gwt.client.ARichtextViewEditWidget;
import ilarkesto.gwt.client.ATextViewEditWidget;
import ilarkesto.gwt.client.ToolbarWidget;
import scrum.client.ScrumGwtApplication;
import scrum.client.common.ABlockWidget;
import scrum.client.common.AExtensibleBlockWidget;
import scrum.client.common.BlockWidgetFactory;
import scrum.client.common.FieldsWidget;
import scrum.client.dnd.ClipboardSupport;
import scrum.client.dnd.TrashSupport;
import scrum.client.img.Img;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class QualityBlock extends AExtensibleBlockWidget<Quality> implements TrashSupport, ClipboardSupport {

	private Quality quality;

	private Label summary;
	private FieldsWidget fields;

	@Override
	protected Quality getObject() {
		return quality;
	}

	@Override
	protected void setObject(Quality object) {
		this.quality = object;
	}

	@Override
	protected void onCollapsedInitialization() {
		summary = new Label();
	}

	@Override
	protected void onCollapsedUpdate() {
		setBlockTitle(quality.getLabel());
		setIcon(Img.bundle.requirement32());
		summary.setText(quality.getLabel());
		setContent(summary);
		setToolbar(null);
	}

	@Override
	protected void onExtendedInitialization() {
		fields = new FieldsWidget();
		fields.setAutoUpdateWidget(this);
		fields.add("Label", new ATextViewEditWidget() {

			@Override
			protected void onViewerUpdate() {
				setViewerText(quality.getLabel());
			}

			@Override
			protected void onEditorUpdate() {
				setEditorText(quality.getLabel());
			}

			@Override
			protected void onEditorSubmit() {
				quality.setLabel(getEditorText());
			}

		});

		fields.add("Description", new ARichtextViewEditWidget() {

			@Override
			protected void onViewerUpdate() {
				setViewerText(quality.getDescription());
			}

			@Override
			protected void onEditorUpdate() {
				setEditorText(quality.getDescription());
			}

			@Override
			protected void onEditorSubmit() {
				quality.setDescription(getEditorText());
			}

		});

		fields.add("Test", new ARichtextViewEditWidget() {

			@Override
			protected void onViewerUpdate() {
				setViewerText(quality.getTestDescription());
			}

			@Override
			protected void onEditorUpdate() {
				setEditorText(quality.getTestDescription());
			}

			@Override
			protected void onEditorSubmit() {
				quality.setTestDescription(getEditorText());
			}

		});
	}

	@Override
	protected void onExtendedUpdate() {
		setBlockTitle(quality.getLabel());
		// Img.bundle.requirement32();
		fields.update();
		setContent(fields);
		setToolbar(createToolbar());
	}

	protected Widget createToolbar() {
		ToolbarWidget toolbar = new ToolbarWidget();

		toolbar.addButton(Img.bundle.delete16().createImage(), "Delete").addClickListener(new ClickListener() {

			public void onClick(Widget sender) {
				ScrumGwtApplication.get().getProject().deleteQuality(quality);
				QualityBacklogWidget.get().list.removeSelectedRow();
			}
		});

		return toolbar;
	}

	public Image getClipboardIcon() {
		return Img.bundle.requirement16().createImage();
	}

	public String getClipboardLabel() {
		return quality.getLabel();
	}

	public ABlockWidget getClipboardPayload() {
		return this;
	}

	public boolean isTrashable() {
		return true;
	}

	public void trash() {
		quality.getProject().deleteQuality(quality);
		getList().removeObject(quality);
	}

	public Quality getQuality() {
		return quality;
	}

	public static BlockWidgetFactory<Quality> FACTORY = new BlockWidgetFactory<Quality>() {

		public QualityBlock createBlock() {
			return new QualityBlock();
		}
	};
}