package scrum.server.impediments;

import ilarkesto.base.time.Date;
import scrum.client.common.LabelSupport;
import scrum.client.common.ReferenceSupport;
import scrum.server.admin.User;
import scrum.server.common.Numbered;

public class Impediment extends GImpediment implements Numbered, ReferenceSupport, LabelSupport {

	@Override
	public void updateNumber() {
		if (getNumber() == 0) setNumber(getProject().generateImpedimentNumber());
	}

	public String getReferenceAndLabel() {
		return getReference() + " " + getLabel();
	}

	@Override
	public String getReference() {
		return scrum.client.impediments.Impediment.REFERENCE_PREFIX + getNumber();
	}

	@Override
	public boolean isVisibleFor(User user) {
		return getProject().isVisibleFor(user);
	}

	public boolean isEditableBy(User user) {
		return getProject().isEditableBy(user);
	}

	@Override
	public void ensureIntegrity() {
		super.ensureIntegrity();
		updateNumber();
		if (!isDateSet()) setDate(Date.today());

		// delete when closed and older than 4 weeks
		if (isClosed() && getDate().getPeriodToNow().toWeeks() > 4) getDao().deleteEntity(this);

	}

	@Override
	public String toString() {
		return getReferenceAndLabel();
	}
}
