package org.openmrs.module.epts.etl.exceptions;

import org.openmrs.module.epts.etl.conf.types.ActionOnEtlIssue;
import org.openmrs.module.epts.etl.model.base.EtlObject;

public class EtlExceptionImpl extends RuntimeException implements EtlException {

	private static final long serialVersionUID = 1L;

	private EtlObject etlObject;

	private ActionOnEtlIssue action;

	public EtlExceptionImpl() {
		this.action = ActionOnEtlIssue.ABORT_PROCESS;
	}

	public EtlExceptionImpl(String msg) {
		super(msg);
	}

	public EtlExceptionImpl(String msg, EtlObject etlObject, ActionOnEtlIssue action) {
		super(msg);

		this.action = action;
		this.etlObject = etlObject;
	}

	public EtlExceptionImpl(String msg, Exception e) {
		super(msg, e);

		this.action = ActionOnEtlIssue.ABORT_PROCESS;
	}

	public EtlExceptionImpl(String msg, Exception e, EtlObject etlObject, ActionOnEtlIssue action) {
		super(msg, e);

		this.action = action;
		this.etlObject = etlObject;
	}

	public EtlExceptionImpl(Exception e) {
		this(e.getLocalizedMessage(), e);

		this.action = ActionOnEtlIssue.ABORT_PROCESS;

	}

	public EtlExceptionImpl(Exception e, EtlObject etlObject, ActionOnEtlIssue action) {
		this(e.getLocalizedMessage(), e);

		this.action = action;
		this.etlObject = etlObject;
	}

	public void setEtlObject(EtlObject etlObject) {
		this.etlObject = etlObject;
	}

	@Override
	public Throwable getException() {
		return this;
	}

	@Override
	public ActionOnEtlIssue getAction() {
		return this.action;
	}

	@Override
	public EtlObject getEtlObject() {
		return this.etlObject;
	}

	@Override
	public String getLocalizedMessage() {
		return (this.etlObject != null ? "Error ocurred o while processing record: " + this.etlObject + "\n" : "")
				+ super.getLocalizedMessage();
	}

	
}
