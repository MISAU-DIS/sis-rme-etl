package org.openmrs.module.epts.etl.exceptions;

import org.openmrs.module.epts.etl.conf.types.ActionOnEtlIssue;
import org.openmrs.module.epts.etl.model.base.EtlObject;
import org.openmrs.module.epts.etl.model.pojo.generic.EtlObjectConfiguration;

public class PojoNotFoundException extends EtlConfException {

	private static final long serialVersionUID = 1414389901658588032L;

	private Object currentObject;
	private EtlObjectConfiguration conf;

	public PojoNotFoundException(EtlObjectConfiguration conf) {
		super("The related POJO class for table " + conf
				+ " cannot be found. Make sure you have run the DATABASE_MODEL_GENERATION operation.");

		this.conf = conf;
	}

	public PojoNotFoundException(String msg) {
		super(msg);
	}

	public PojoNotFoundException(String msg, Object currentObject) {
		super(msg);

		this.currentObject = currentObject;
	}

	public PojoNotFoundException(String msg, Exception exception) {
		super(msg, exception);
	}

	public PojoNotFoundException(Exception exception) {
		super(exception);
	}

	public EtlObjectConfiguration getConf() {
		return conf;
	}

	public void setConf(EtlObjectConfiguration conf) {
		this.conf = conf;
	}

	public Object getCurrentObject() {
		return currentObject;
	}

	public void setCurrentObject(Object currentObject) {
		this.currentObject = currentObject;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public Throwable getException() {
		return this;
	}

	@Override
	public ActionOnEtlIssue getAction() {
		return ActionOnEtlIssue.ABORT_PROCESS;
	}

	@Override
	public EtlObject getEtlObject() {
		return null;
	}
}
