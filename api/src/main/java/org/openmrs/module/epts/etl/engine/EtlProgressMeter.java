package org.openmrs.module.epts.etl.engine;

import java.util.Date;

import org.openmrs.module.epts.etl.conf.types.EtlOperationStatus;
import org.openmrs.module.epts.etl.exceptions.EtlExceptionImpl;
import org.openmrs.module.epts.etl.utilities.CommonUtilities;
import org.openmrs.module.epts.etl.utilities.DateAndTimeUtilities;
import org.openmrs.module.epts.etl.utilities.ObjectMapperProvider;
import org.openmrs.module.epts.etl.utilities.concurrent.TimeController;
import org.openmrs.module.epts.etl.utilities.concurrent.TimeCountDown;
import org.openmrs.module.epts.etl.utilities.concurrent.TimeCountDownInitializer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;

public class EtlProgressMeter implements TimeCountDownInitializer {
	
	private CommonUtilities utilities = CommonUtilities.getInstance();
	
	public final Object LOCK = new Object();
	
	private String id;
	
	private String designation;
	
	/**
	 * Total de registos
	 */
	private int total;
	
	private long minRecordId;
	
	private long maxRecordId;
	
	/**
	 * The last analyzed record in interval {@link #minRecordId} - {@link #maxRecordId}
	 */
	private long lastAnalyzedRecordId;
	
	/**
	 * Estado corrente
	 */
	private EtlOperationStatus status;
	
	/**
	 * Registos processados
	 */
	private int processed;
	
	/**
	 * Mensagem do estado corrente da migracao
	 */
	private String statusMsg;
	
	/**
	 * Indica se existe um erro
	 */
	private boolean statusError;
	
	//private boolean _default;
	
	private TimeCountDown updateControl;
	
	/**
	 * Indica se este {@link MigrationProgressMeter} encontra-se actualizado ou nao.
	 * <p>
	 * Com base no valor deste atributo pode se evitar o "refresh" sempre que o mesmo se encontrar
	 * updated
	 */
	private boolean updated;
	
	/**
	 * Indica o intervalo de tempo durante o qual este {@link EtlProgressMeter} sera considerado
	 * updated
	 */
	private int refreshInterval;
	
	private Date startTime;
	
	private Date finishTime;
	
	private TimeController processingTimer;
	
	private TimeController pauseTimer;
	
	private TimeController totalTimer;
	
	public EtlProgressMeter() {
		this.status = EtlOperationStatus.NOT_INITIALIZED;
	}
	
	public EtlProgressMeter(String statusMsg, long minRecordId, long maxRecordId, int total, int processed,
	    long lastAnalyzedRecordId) {
		
		this();
		
		this.minRecordId = minRecordId;
		this.maxRecordId = maxRecordId;
		this.lastAnalyzedRecordId = lastAnalyzedRecordId;
		
		refresh(statusMsg, total, processed, lastAnalyzedRecordId);
		
		this.id = "meter_default_id" + this.hashCode();
		
		//Assume-se que por omissao qualquer meter encontra-se desactualizado, de tal maneiras que 
		//qualquer chamada ao refresh deve ser executada
		this.updated = false;
	}
	
	private EtlProgressMeter(String id) {
		this();
		
		this.id = id;
	}
	
	public TimeController getProcessingTimer() {
		return processingTimer;
	}
	
	public TimeController getPauseTimer() {
		return pauseTimer;
	}
	
	public TimeController getTotalTimer() {
		return totalTimer;
	}
	
	public long getLastAnalyzedRecordId() {
		return lastAnalyzedRecordId;
	}
	
	public void setLastAnalyzedRecordId(long lastAnalyzedRecordId) {
		this.lastAnalyzedRecordId = lastAnalyzedRecordId;
	}
	
	public static EtlProgressMeter defaultProgressMeter(String id) {
		return new EtlProgressMeter(id);
	}
	
	public Date getFinishTime() {
		return finishTime;
	}
	
	public void setFinishTime(Date finishTime) {
		this.finishTime = finishTime;
	}
	
	public EtlOperationStatus getStatus() {
		return status;
	}
	
	public void setProcessed(int processed) {
		this.processed = processed;
	}
	
	/**
	 * Refresca a informacao do estado actual da migracao, recalcunlando a percentagem de progresso
	 * 
	 * @param statusMsg Mensagem do corrente estado da migracao
	 * @param total de registos em migracao
	 * @param processed Registos processados
	 * @param timer Temporizador da migracao
	 * @param lastAnalyzedRecordId the last Analyzed recordId
	 */
	public synchronized void refresh(String statusMsg, int total, int processed, long lastAnalyzedRecordId) {
		this.total = total;
		this.processed = processed;
		this.lastAnalyzedRecordId = lastAnalyzedRecordId;
		
		this.statusMsg = statusMsg;
		
		if (refreshInterval > 0 && this.updateControl == null) {
			this.updateControl = TimeCountDown.wait(this, 60 * this.refreshInterval, "");
			this.updated = true;
		} else if (this.updateControl != null) {
			this.updated = true;
			
			this.updateControl.restart();
		}
	}
	
	@JsonIgnore
	public String getHumanReadbleProcessingTime() {
		return getProcessingTimer() != null ? getProcessingTimer().toString() : "00:00:00";
	}
	
	@JsonIgnore
	public String getHumanReadblePauseTime() {
		return getPauseTimer() != null ? getPauseTimer().toString() : "00:00:00";
	}
	
	@JsonIgnore
	public String getHumanReadbleTotalTime() {
		return getTotalTimer() != null ? getTotalTimer().toString() : "00:00:00";
	}
	
	@JsonIgnore
	public String getHumanReadbleEstimatedRemainingTime() {
		if (this.getProcessed() == 0) {
			return "UNKNOW";
		} else {
			
			double eta = (getTotal() * getProcessingTimer().getElapsedSeconds()) / this.getProcessed();
			
			TimeController tc = new TimeController(eta);
			
			return tc.toString();
		}
	}
	
	public void changeRefreshInterval(int refreshInterval) {
		this.refreshInterval = refreshInterval;
		
		if (refreshInterval > 0) {} else {
			this.updateControl = null;
			this.updated = false;
		}
	}
	
	/**
	 * @return a hora de inicio da migracao
	 */
	public Date getStartTime() {
		return this.startTime;
	}
	
	/**
	 * @return a hora de inicio da migracao formatada
	 */
	public String getFormatedStartTime() {
		return getStartTime() != null ? DateAndTimeUtilities.formatToDDMMYYYY_HHMISS(getStartTime()) : null;
	}
	
	/**
	 * @param exception de erro na migracao
	 */
	public void reportError(Exception exception) {
		this.statusMsg = exception.getLocalizedMessage();
		statusError = true;
	}
	
	/**
	 * @return the statusError
	 */
	public boolean isStatusError() {
		return statusError;
	}
	
	/**
	 * @return the statusMsg
	 */
	public String getStatusMsg() {
		return statusMsg;
	}
	
	/**
	 * @return the total
	 */
	public int getTotal() {
		return (int) total;
	}
	
	/**
	 * @return Quantidade de registos processados
	 */
	public int getProcessed() {
		return (int) processed;
	}
	
	public int getRemain() {
		return this.total - this.processed;
	}
	
	public int getTotalToAnalyze() {
		return (int) (this.maxRecordId - this.minRecordId);
	}
	
	public int getTotalAnalyzed() {
		return (int) (this.lastAnalyzedRecordId - this.minRecordId) + 1;
	}
	
	public int getRemainToAnalyze() {
		return getTotalToAnalyze() - getTotalAnalyzed();
	}
	
	public double getProgressOfAnalyzedRecords() {
		double progress = 0;
		
		if (this.getTotalAnalyzed() > 0) {
			
			double processedAsDouble = this.getTotalAnalyzed();
			double totalAsDouble = this.getTotalToAnalyze();
			
			progress = Double.parseDouble(utilities.getNumberInXPrecision((processedAsDouble / totalAsDouble) * 100, 2));
		}
		
		return progress;
	}
	
	/**
	 * @return a percentagem de progresso da migracao
	 */
	public double getProgress() {
		double progress = 0;
		
		if (this.total > 0) {
			
			double processedAsDouble = this.processed;
			double totalAsDouble = this.total;
			
			progress = Double.parseDouble(utilities.getNumberInXPrecision((processedAsDouble / totalAsDouble) * 100, 2));
		}
		
		return progress;
	}
	
	public boolean isRunning() {
		return this.status.running();
	}
	
	public boolean isPaused() {
		return this.status.paused();
	}
	
	public boolean isStopped() {
		return this.status.stopped();
	}
	
	public boolean isSleeping() {
		return this.status.slepping();
	}
	
	public boolean isFinished() {
		return this.status.finished();
	}
	
	public void changeStatusToSleeping() {
		this.status = EtlOperationStatus.SLEEPING;
		this.statusMsg = EtlOperationStatus.SLEEPING.getDsc();
		
	}
	
	public void changeStatusToRunning() {
		this.status = EtlOperationStatus.RUNNING;
		this.statusMsg = EtlOperationStatus.RUNNING.getDsc();
		
		tryToInitializeTimeControllers();
		
		this.getProcessingTimer().start();
		this.getTotalTimer().start();
		//this.getPauseTimer().stop();
	}
	
	public void changeStatusToStopping() {
		this.status = EtlOperationStatus.STOPPED;
		this.statusMsg = EtlOperationStatus.STOPPED.getDsc();
	}
	
	public void changeStatusToStopped() {
		this.status = EtlOperationStatus.STOPPED;
		this.statusMsg = EtlOperationStatus.STOPPED.getDsc();
		
		tryToInitializeTimeControllers();
		
		this.getProcessingTimer().stop();
		this.getTotalTimer().stop();
		//this.getPauseTimer().start();
	}
	
	public void changeStatusToFinished() {
		this.status = EtlOperationStatus.FINISHED;
		this.statusMsg = EtlOperationStatus.FINISHED.getDsc();
		
		this.finishTime = DateAndTimeUtilities.getCurrentDate();
		
		tryToInitializeTimeControllers();
		
		this.getTotalTimer().stop();
		this.getProcessingTimer().stop();
	}
	
	private void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	
	private void tryToInitializeTimeControllers() {
		
		synchronized (LOCK) {
			if (this.totalTimer == null) {
				this.totalTimer = new TimeController();
			}
			
			if (this.processingTimer == null) {
				this.processingTimer = new TimeController();
			}
			
			if (this.pauseTimer == null) {
				this.pauseTimer = new TimeController();
			}
			
			if (this.getStartTime() == null) {
				this.setStartTime(DateAndTimeUtilities.getCurrentDate());
			}
		}
	}
	
	@JsonIgnore
	public String parseToJSON() {
		try {
			return new ObjectMapperProvider().getContext(EtlProgressMeter.class).writeValueAsString(this);
		}
		catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * @return o valor do atributo {@link #id}
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Modifica o valor do atributo {@link #id} para o valor fornecido pelo parâmetro
	 * <code>id</code>
	 * 
	 * @param id novo valor para o atributo {@link #id}
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof String) {
			return utilities.isStringIn(this.id, obj.toString());
		}
		
		if (!(obj instanceof EtlProgressMeter))
			return false;
		
		EtlProgressMeter otherMeter = (EtlProgressMeter) obj;
		
		if (utilities.isStringIn(this.id, otherMeter.id))
			return true;
		
		return false;
	}
	
	public String getDetailedRemaining() {
		int remaining = total - processed;
		
		return identQty(utilities.generateCommaSeparetedNumber(remaining)) + "("
		        + identPercentage("" + formatPercentage((100 - this.getProgress()))) + "%)";
	}
	
	public String getDetailedProgress() {
		return identQty(utilities.generateCommaSeparetedNumber(this.processed)) + "("
		        + identPercentage("" + formatPercentage(this.getProgress())) + "%)";
	}
	
	public String getDetailedRemainingToAnalize() {
		int remaining = this.getRemainToAnalyze();
		
		return identQty(utilities.generateCommaSeparetedNumber(remaining)) + "("
		        + identPercentage("" + formatPercentage((100 - this.getProgressOfAnalyzedRecords()))) + "%)";
	}
	
	public String getDetailedProgressOfAnalyzedRecords() {
		return identQty(utilities.generateCommaSeparetedNumber(this.getTotalAnalyzed())) + "("
		        + identPercentage("" + formatPercentage(this.getProgressOfAnalyzedRecords())) + "%)";
	}
	
	/**
	 * @return o valor do atributo {@link #designation}
	 */
	public String getDesignation() {
		return designation;
	}
	
	/**
	 * Modifica o valor do atributo {@link #designation} para o valor fornecido pelo parâmetro
	 * <code>designation</code>
	 * 
	 * @param designation novo valor para o atributo {@link #designation}
	 */
	public void setDesignation(String designation) {
		this.designation = designation;
	}
	
	@Override
	@JsonIgnore
	public String getThreadNamingPattern() {
		String pathern = "[" + this.getClass().getCanonicalName() + "]" + "[%d]";
		
		pathern = "[ProgressMeter]" + pathern;
		
		return pathern;
	}
	
	/**
	 * Indica se este meter esta actualizado ou nao.
	 * <p>
	 * Cada meter pode definir um intervalo dentro do qual o mesmo pode ser actualizado Qualquer
	 * tentativa de "actualizacao" de um meter que esteja "actualizado" podera ser ignorado evitando
	 * dessa forma consumo de recursos desnecessario
	 * 
	 * @return true se este meter se encontrar actualizado ou false no caso contrario
	 */
	public boolean isUpdated() {
		return this.updated;
	}
	
	@Override
	public void onFinish() {
		this.updated = false;
	}
	
	public static EtlProgressMeter fullInit(EtlOperationStatus status, Date startTime, Date lastStopTime,
	        double processingTime, double pauseTime, long minRecordId, long maxRecordId, int total, int processed) {
		
		EtlProgressMeter progressMeter = new EtlProgressMeter();
		
		progressMeter.status = status;
		progressMeter.startTime = startTime;
		progressMeter.minRecordId = minRecordId;
		progressMeter.maxRecordId = maxRecordId;
		progressMeter.total = total;
		progressMeter.processed = processed;
		
		double currentPauseTime = pauseTime;
		
		if (lastStopTime != null && shouldAccumulatePauseTime(status)) {
			double elapsedPauseTime = DateAndTimeUtilities.dateDiff(DateAndTimeUtilities.getCurrentDate(), lastStopTime,
			    DateAndTimeUtilities.SECOND_FORMAT);
			
			if (elapsedPauseTime > 0) {
				currentPauseTime += elapsedPauseTime;
			}
		}
		
		double totalTime = processingTime + currentPauseTime;
		
		progressMeter.totalTimer = new TimeController(totalTime);
		progressMeter.processingTimer = new TimeController(processingTime);
		progressMeter.pauseTimer = new TimeController(currentPauseTime);
		
		if (progressMeter.isFinished()) {
			progressMeter.finishTime = lastStopTime;
		}
		
		return progressMeter;
	}
	
	private static boolean shouldAccumulatePauseTime(EtlOperationStatus status) {
		return status != null && !status.finished();
	}
	
	public long getMinRecordId() {
		return minRecordId;
	}
	
	public void setMinRecordId(long minRecordId) {
		this.minRecordId = minRecordId;
	}
	
	public long getMaxRecordId() {
		return maxRecordId;
	}
	
	public void setMaxRecordId(long maxRecordId) {
		this.maxRecordId = maxRecordId;
	}
	
	String identQty(String qty) {
		return utilities.ident(qty, 12);
	}
	
	String identPercentage(String percentage) {
		return utilities.ident(percentage, 6);
	}
	
	String formatPercentage(double percentage) {
		return utilities.getNumberInXPrecision(percentage, 2);
	}
	
	public void changeStatus(EtlOperationStatus status) {
		
		switch (status) {
			case RUNNING:
				changeStatusToRunning();
				break;
			case STOPPED:
				changeStatusToStopped();
				break;
			case FINISHED:
				changeStatusToFinished();
				break;
			case SLEEPING:
				changeStatusToSleeping();
				break;
			case STOPPED_DUE_ERROR:
				changeStatusToStopped();
				break;
			
			case STOPPING:
				changeStatusToStopped();
				break;
			
			default:
				throw new EtlExceptionImpl("Unsupported status " + status);
		}
		
	}
}
