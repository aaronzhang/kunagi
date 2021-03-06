// ----------> GENERATED FILE - DON'T TOUCH! <----------

// generator: ilarkesto.mda.legacy.generator.GwtEntityGenerator










package scrum.client.admin;

import java.util.*;
import ilarkesto.persistence.*;
import ilarkesto.core.logging.Log;
import ilarkesto.base.*;
import ilarkesto.base.time.*;
import ilarkesto.auth.*;
import scrum.client.common.*;
import ilarkesto.gwt.client.*;

public abstract class GProjectUserConfig
            extends scrum.client.common.AScrumGwtEntity {

    protected scrum.client.Dao getDao() {
        return scrum.client.Dao.get();
    }

    public abstract boolean isMisconductsEditable();

    public GProjectUserConfig() {
    }

    public GProjectUserConfig(Map data) {
        super(data);
        updateProperties(data);
    }

    public static final String ENTITY_TYPE = "projectUserConfig";

    @Override
    public final String getEntityType() {
        return ENTITY_TYPE;
    }

    // --- project ---

    private String projectId;

    public final scrum.client.project.Project getProject() {
        if (projectId == null) return null;
        return getDao().getProject(this.projectId);
    }

    public final boolean isProjectSet() {
        return projectId != null;
    }

    public final ProjectUserConfig setProject(scrum.client.project.Project project) {
        String id = project == null ? null : project.getId();
        if (equals(this.projectId, id)) return (ProjectUserConfig) this;
        this.projectId = id;
        propertyChanged("projectId", this.projectId);
        return (ProjectUserConfig)this;
    }

    public final boolean isProject(scrum.client.project.Project project) {
        return equals(this.projectId, project);
    }

    // --- user ---

    private String userId;

    public final scrum.client.admin.User getUser() {
        if (userId == null) return null;
        return getDao().getUser(this.userId);
    }

    public final boolean isUserSet() {
        return userId != null;
    }

    public final ProjectUserConfig setUser(scrum.client.admin.User user) {
        String id = user == null ? null : user.getId();
        if (equals(this.userId, id)) return (ProjectUserConfig) this;
        this.userId = id;
        propertyChanged("userId", this.userId);
        return (ProjectUserConfig)this;
    }

    public final boolean isUser(scrum.client.admin.User user) {
        return equals(this.userId, user);
    }

    // --- color ---

    private java.lang.String color ;

    public final java.lang.String getColor() {
        return this.color ;
    }

    public final ProjectUserConfig setColor(java.lang.String color) {
        if (isColor(color)) return (ProjectUserConfig)this;
        this.color = color ;
        propertyChanged("color", this.color);
        return (ProjectUserConfig)this;
    }

    public final boolean isColor(java.lang.String color) {
        return equals(this.color, color);
    }

    private transient ColorModel colorModel;

    public ColorModel getColorModel() {
        if (colorModel == null) colorModel = createColorModel();
        return colorModel;
    }

    protected ColorModel createColorModel() { return new ColorModel(); }

    protected class ColorModel extends ilarkesto.gwt.client.editor.ATextEditorModel {

        @Override
        public String getId() {
            return "ProjectUserConfig_color";
        }

        @Override
        public java.lang.String getValue() {
            return getColor();
        }

        @Override
        public void setValue(java.lang.String value) {
            setColor(value);
        }

        @Override
        protected void onChangeValue(java.lang.String oldValue, java.lang.String newValue) {
            super.onChangeValue(oldValue, newValue);
            addUndo(this, oldValue);
        }

    }

    // --- misconducts ---

    private int misconducts ;

    public final int getMisconducts() {
        return this.misconducts ;
    }

    public final ProjectUserConfig setMisconducts(int misconducts) {
        if (isMisconducts(misconducts)) return (ProjectUserConfig)this;
        this.misconducts = misconducts ;
        propertyChanged("misconducts", this.misconducts);
        return (ProjectUserConfig)this;
    }

    public final boolean isMisconducts(int misconducts) {
        return equals(this.misconducts, misconducts);
    }

    private transient MisconductsModel misconductsModel;

    public MisconductsModel getMisconductsModel() {
        if (misconductsModel == null) misconductsModel = createMisconductsModel();
        return misconductsModel;
    }

    protected MisconductsModel createMisconductsModel() { return new MisconductsModel(); }

    protected class MisconductsModel extends ilarkesto.gwt.client.editor.AIntegerEditorModel {

        @Override
        public String getId() {
            return "ProjectUserConfig_misconducts";
        }

        @Override
        public java.lang.Integer getValue() {
            return getMisconducts();
        }

        @Override
        public void setValue(java.lang.Integer value) {
            setMisconducts(value);
        }

            @Override
            public void increment() {
                setMisconducts(getMisconducts() + 1);
            }

            @Override
            public void decrement() {
                setMisconducts(getMisconducts() - 1);
            }

        @Override
        public boolean isEditable() { return GProjectUserConfig.this.isMisconductsEditable(); }

        @Override
        protected void onChangeValue(java.lang.Integer oldValue, java.lang.Integer newValue) {
            super.onChangeValue(oldValue, newValue);
            addUndo(this, oldValue);
        }

    }

    // --- richtextAutosaveText ---

    private java.lang.String richtextAutosaveText ;

    public final java.lang.String getRichtextAutosaveText() {
        return this.richtextAutosaveText ;
    }

    public final ProjectUserConfig setRichtextAutosaveText(java.lang.String richtextAutosaveText) {
        if (isRichtextAutosaveText(richtextAutosaveText)) return (ProjectUserConfig)this;
        this.richtextAutosaveText = richtextAutosaveText ;
        propertyChanged("richtextAutosaveText", this.richtextAutosaveText);
        return (ProjectUserConfig)this;
    }

    public final boolean isRichtextAutosaveText(java.lang.String richtextAutosaveText) {
        return equals(this.richtextAutosaveText, richtextAutosaveText);
    }

    private transient RichtextAutosaveTextModel richtextAutosaveTextModel;

    public RichtextAutosaveTextModel getRichtextAutosaveTextModel() {
        if (richtextAutosaveTextModel == null) richtextAutosaveTextModel = createRichtextAutosaveTextModel();
        return richtextAutosaveTextModel;
    }

    protected RichtextAutosaveTextModel createRichtextAutosaveTextModel() { return new RichtextAutosaveTextModel(); }

    protected class RichtextAutosaveTextModel extends ilarkesto.gwt.client.editor.ATextEditorModel {

        @Override
        public String getId() {
            return "ProjectUserConfig_richtextAutosaveText";
        }

        @Override
        public java.lang.String getValue() {
            return getRichtextAutosaveText();
        }

        @Override
        public void setValue(java.lang.String value) {
            setRichtextAutosaveText(value);
        }

        @Override
        protected void onChangeValue(java.lang.String oldValue, java.lang.String newValue) {
            super.onChangeValue(oldValue, newValue);
            addUndo(this, oldValue);
        }

    }

    // --- richtextAutosaveField ---

    private java.lang.String richtextAutosaveField ;

    public final java.lang.String getRichtextAutosaveField() {
        return this.richtextAutosaveField ;
    }

    public final ProjectUserConfig setRichtextAutosaveField(java.lang.String richtextAutosaveField) {
        if (isRichtextAutosaveField(richtextAutosaveField)) return (ProjectUserConfig)this;
        this.richtextAutosaveField = richtextAutosaveField ;
        propertyChanged("richtextAutosaveField", this.richtextAutosaveField);
        return (ProjectUserConfig)this;
    }

    public final boolean isRichtextAutosaveField(java.lang.String richtextAutosaveField) {
        return equals(this.richtextAutosaveField, richtextAutosaveField);
    }

    private transient RichtextAutosaveFieldModel richtextAutosaveFieldModel;

    public RichtextAutosaveFieldModel getRichtextAutosaveFieldModel() {
        if (richtextAutosaveFieldModel == null) richtextAutosaveFieldModel = createRichtextAutosaveFieldModel();
        return richtextAutosaveFieldModel;
    }

    protected RichtextAutosaveFieldModel createRichtextAutosaveFieldModel() { return new RichtextAutosaveFieldModel(); }

    protected class RichtextAutosaveFieldModel extends ilarkesto.gwt.client.editor.ATextEditorModel {

        @Override
        public String getId() {
            return "ProjectUserConfig_richtextAutosaveField";
        }

        @Override
        public java.lang.String getValue() {
            return getRichtextAutosaveField();
        }

        @Override
        public void setValue(java.lang.String value) {
            setRichtextAutosaveField(value);
        }

        @Override
        protected void onChangeValue(java.lang.String oldValue, java.lang.String newValue) {
            super.onChangeValue(oldValue, newValue);
            addUndo(this, oldValue);
        }

    }

    // --- selectedEntitysIds ---

    private java.util.List<java.lang.String> selectedEntitysIds = new java.util.ArrayList<java.lang.String>();

    public final java.util.List<java.lang.String> getSelectedEntitysIds() {
        return new java.util.ArrayList<java.lang.String>(selectedEntitysIds);
    }

    public final void setSelectedEntitysIds(java.util.List<java.lang.String> selectedEntitysIds) {
        if (selectedEntitysIds == null) throw new IllegalArgumentException("null is not allowed");
        if (this.selectedEntitysIds.equals(selectedEntitysIds)) return;
        this.selectedEntitysIds = new java.util.ArrayList<java.lang.String>(selectedEntitysIds);
        propertyChanged("selectedEntitysIds", this.selectedEntitysIds);
    }

    // --- online ---

    private boolean online ;

    public final boolean isOnline() {
        return this.online ;
    }

    public final ProjectUserConfig setOnline(boolean online) {
        if (isOnline(online)) return (ProjectUserConfig)this;
        this.online = online ;
        propertyChanged("online", this.online);
        return (ProjectUserConfig)this;
    }

    public final boolean isOnline(boolean online) {
        return equals(this.online, online);
    }

    private transient OnlineModel onlineModel;

    public OnlineModel getOnlineModel() {
        if (onlineModel == null) onlineModel = createOnlineModel();
        return onlineModel;
    }

    protected OnlineModel createOnlineModel() { return new OnlineModel(); }

    protected class OnlineModel extends ilarkesto.gwt.client.editor.ABooleanEditorModel {

        @Override
        public String getId() {
            return "ProjectUserConfig_online";
        }

        @Override
        public java.lang.Boolean getValue() {
            return isOnline();
        }

        @Override
        public void setValue(java.lang.Boolean value) {
            setOnline(value);
        }

        @Override
        protected void onChangeValue(java.lang.Boolean oldValue, java.lang.Boolean newValue) {
            super.onChangeValue(oldValue, newValue);
            addUndo(this, oldValue);
        }

    }

    // --- update properties by map ---

    public void updateProperties(Map props) {
        projectId = (String) props.get("projectId");
        userId = (String) props.get("userId");
        color  = (java.lang.String) props.get("color");
        misconducts  = (Integer) props.get("misconducts");
        richtextAutosaveText  = (java.lang.String) props.get("richtextAutosaveText");
        richtextAutosaveField  = (java.lang.String) props.get("richtextAutosaveField");
        selectedEntitysIds  = (java.util.List<java.lang.String>) props.get("selectedEntitysIds");
        online  = (Boolean) props.get("online");
        updateLocalModificationTime();
    }

    @Override
    public void storeProperties(Map properties) {
        super.storeProperties(properties);
        properties.put("projectId", this.projectId);
        properties.put("userId", this.userId);
        properties.put("color", this.color);
        properties.put("misconducts", this.misconducts);
        properties.put("richtextAutosaveText", this.richtextAutosaveText);
        properties.put("richtextAutosaveField", this.richtextAutosaveField);
        properties.put("selectedEntitysIds", this.selectedEntitysIds);
        properties.put("online", this.online);
    }

}