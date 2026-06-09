package org.openmrs.module.epts.etl.conf.datasource;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openmrs.module.epts.etl.conf.EtlConfiguration;
import org.openmrs.module.epts.etl.conf.GenericTableConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.EtlAdditionalDataSource;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataSource;
import org.openmrs.module.epts.etl.conf.interfaces.TableAliasesGenerator;
import org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration;
import org.openmrs.module.epts.etl.conf.types.DbmsType;
import org.openmrs.module.epts.etl.engine.Engine;
import org.openmrs.module.epts.etl.engine.record_intervals_manager.IntervalExtremeRecord;
import org.openmrs.module.epts.etl.etl.processor.EtlProcessor;
import org.openmrs.module.epts.etl.exceptions.EtlExceptionImpl;
import org.openmrs.module.epts.etl.exceptions.FieldAvaliableInMultipleDataSources;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.exceptions.MissingParameterException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.model.pojo.generic.DatabaseObjectDAO;
import org.openmrs.module.epts.etl.utilities.CommonUtilities;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;
import org.openmrs.module.epts.etl.utilities.db.conn.SQLUtilities;

/**
 * Represents an prepared query ready to be executed. It alwas has a ready query and its parameters
 */
public class PreparedQuery {
	
	public static CommonUtilities utilities = CommonUtilities.getInstance();
	
	private String query;
	
	private List<EtlDataSource> avaliableDataSources;
	
	private EtlConfiguration etlConfig;
	
	private List<QueryParameter> queryParams;
	
	private String mainQuery;
	
	private List<String> subqueries;
	
	private SqlFunctionInfo countFunctionInfo;
	
	private EtlDataSource dataSource;
	
	private String originalQuery;
	
	private boolean sqlFunctionLoaded;
	
	private DbmsType dbmsType;
	
	private boolean original;
	
	private boolean ensuredDynamicElementsLoaded;
	
	PreparedQuery() {
	}
	
	EtlConfiguration getRelatedEtlConfiguration() {
		return getDataSource().getRelatedEtlConf();
	}
	
	PreparedQuery(EtlDataSource dataSource, List<EtlDataSource> avaliableDataSources, EtlConfiguration configuration,
	    boolean ignoreMissingParameters, DbmsType dbmsType) {
		this.dbmsType = dbmsType;
		this.setDataSource(dataSource);
		
		this.logTrace("Starting Query preparation... " + dataSource.getName());
		
		this.original = true;
		
		this.setQuery(dataSource.getQuery());
		this.setEtlConfig(configuration);
		this.setAvaliableDataSources(avaliableDataSources);
		this.tryToLoadSQLFunctionInfo();
		
		setQuery(ensureDynamicElementsLoadedAsParameteres(this.getQuery()));
		setQuery(autoDefineDataSourceParameters(this.getQuery()));
		
		this.setQueryParams(extractParamOnQuery(this.getQuery()));
		
		logTrace("Query Parameters Loaded!");
	}
	
	public boolean hasAvaliableDataSources() {
		return utilities.listHasElement(avaliableDataSources);
	}
	
	public List<EtlDataSource> getAvaliableDataSources() {
		return avaliableDataSources;
	}
	
	public void setAvaliableDataSources(List<EtlDataSource> avaliableDataSources) {
		this.avaliableDataSources = avaliableDataSources;
	}
	
	public boolean isEnsuredDynamicElementsLoaded() {
		return ensuredDynamicElementsLoaded;
	}
	
	public boolean isOriginal() {
		return original;
	}
	
	PreparedQuery(EtlAdditionalDataSource queryDs, EtlConfiguration config, boolean ignoreMissingParameters,
	    DbmsType dbmsType) {
		this(queryDs, null, config, ignoreMissingParameters, dbmsType);
	}
	
	void logTrace(String msg) {
		getRelatedEtlConfiguration().logTrace(msg);
	}
	
	void logDebug(String msg) {
		getRelatedEtlConfiguration().logDebug(msg);
	}
	
	public void setQuery(String query) {
		this.originalQuery = query;
		this.query = utilities.removeDuplicatedEmptySpace(this.getOriginalQuery());
		this.query = utilities.removeSpacesBeforeAndAfterPeriod(this.getQuery());
		
		this.query = this.getQuery().replaceAll("\\s+", " ");
		
		logTrace("Discovering subqueries on quey");
		
		this.setSubqueries(SQLUtilities.findSubqueries(this.getQuery()));
		
		if (hasSubQueries()) {
			logTrace("Found Subqueries \n" + this.getSubqueries());
		} else {
			logTrace("No subquery found");
		}
		
		setMainQuery(this.getQuery());
		
		if (hasSubQueries()) {
			logTrace("Masking found Subqueries");
			for (String sQuery : this.getSubqueries()) {
				this.setMainQuery(utilities.maskToken(this.getMainQuery(), sQuery, '#'));
			}
			logTrace("Masking done");
		}
	}
	
	public EtlDataSource getDataSource() {
		return dataSource;
	}
	
	public void setDataSource(EtlDataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	private void setMainQuery(String mainQuery) {
		this.mainQuery = mainQuery;
	}
	
	private String getMainQuery() {
		return mainQuery;
	}
	
	private boolean hasSubQueries() {
		return utilities.listHasElement(this.getSubqueries());
	}
	
	private List<String> getSubqueries() {
		return subqueries;
	}
	
	private void setSubqueries(List<String> subqueries) {
		this.subqueries = subqueries;
	}
	
	private List<QueryParameter> getQueryParams() {
		return queryParams;
	}
	
	private EtlConfiguration getEtlConfig() {
		return etlConfig;
	}
	
	public void setEtlConfig(EtlConfiguration etlConfig) {
		this.etlConfig = etlConfig;
	}
	
	public String getQuery() {
		return query;
	}
	
	public String getOriginalQuery() {
		return originalQuery;
	}
	
	public void setOriginalQuery(String originalQuery) {
		this.originalQuery = originalQuery;
	}
	
	private void setQueryParams(List<QueryParameter> queryParams) {
		this.queryParams = queryParams;
	}
	
	public PreparedQueryInfo generatePreparedQuery(EtlProcessor processor, EtlDatabaseObject srcObject,
	        EtlDatabaseObject dstObject, List<EtlDatabaseObject> avaliableSrcObjects, Connection conn)
	        throws FieldAvaliableInMultipleDataSources, DBException {
		
		return generatePreparedQuery(processor, srcObject, dstObject, avaliableSrcObjects, this.query, conn);
	}
	
	public PreparedQueryInfo generatePreparedQuery(EtlProcessor processor, EtlDatabaseObject srcObject,
	        EtlDatabaseObject dstObject, List<EtlDatabaseObject> avaliableSrcObjects, String query, Connection conn)
	        throws FieldAvaliableInMultipleDataSources, DBException {
		
		List<Object> params = new ArrayList<>();
		
		String pQuery = query;
		
		if (hasQueryParams()) {
			
			for (QueryParameter param : this.getQueryParams()) {
				
				Object paramValue = param.retrieveParamValue(processor, srcObject, dstObject, avaliableSrcObjects, conn);
				
				if (param.getContextType().compareClause() || param.getContextType().selectField()
				        || param.getContextType().inClause()) {
					
					params.add(paramValue);
					
					pQuery = SQLUtilities.replaceFirstParameterOccurrence(pQuery, param.getName());
					
				} else if (param.getContextType().dbResource()) {
					
					if (paramValue == null) {
						throw new ForbiddenOperationException("The parameter '" + param.getName()
						        + "' has no value and is needed to generate prepared query.");
					}
					
					pQuery = SQLUtilities.replaceParameterWithValue(pQuery, param.getName(), paramValue);
				}
			}
		}
		
		return new PreparedQueryInfo(pQuery, params);
	}
	
	public static PreparedQuery prepare(EtlDataSource queryDs, List<EtlDataSource> avaliableDataSource,
	        EtlConfiguration configuration, DbmsType dbmsType) {
		
		return new PreparedQuery(queryDs, avaliableDataSource, configuration, false, dbmsType);
	}
	
	public static PreparedQuery prepare(EtlAdditionalDataSource queryDs, EtlConfiguration etlConfig,
	        List<EtlDataSource> avaliableDataSource, boolean ignoreMissingParameters, DbmsType dbmsType)
	        throws ForbiddenOperationException {
		
		return new PreparedQuery(queryDs, avaliableDataSource, etlConfig, ignoreMissingParameters, dbmsType);
	}
	
	private boolean hasDynamicElements() {
		return this.getDataSource().hasDynamicElements();
	}
	
	private void setCountFunctionInfo(SqlFunctionInfo countFunctionInfo) {
		this.countFunctionInfo = countFunctionInfo;
	}
	
	public SqlFunctionInfo getCountFunctionInfo() {
		return countFunctionInfo;
	}
	
	public boolean isSqlFunctionLoaded() {
		return sqlFunctionLoaded;
	}
	
	public void setSqlFunctionLoaded(boolean sqlFunctionLoaded) {
		this.sqlFunctionLoaded = sqlFunctionLoaded;
	}
	
	private void tryToLoadSQLFunctionInfo() {
		
		if (!isSqlFunctionLoaded()) {
			
			List<SqlFunctionInfo> avaliableFunction = SQLUtilities.extractSqlFunctionsInSelect(getMainQuery());
			
			if (utilities.listHasExactlyOneElement(this.getDataSource().getFields())
			        && utilities.listHasExactlyOneElement(avaliableFunction)) {
				
				if (avaliableFunction.get(0).isCountFunction()) {
					
					utilities.throwForbiddenMethodException();
					
					this.setCountFunctionInfo(avaliableFunction.get(0));
					
					String mainTableName = SQLUtilities.extractFirstTableFromSelectQuery(this.getMainQuery());
					
					if (mainTableName.startsWith("@")) {
						//mainTableName = retrieveParamValue(utilities.removeFirsChar(mainTableName)).toString();
					}
					
					this.getCountFunctionInfo().setMainTable(new GenericTableConfiguration(mainTableName));
					
					new MaintableAliasGenerator(this, mainTableName)
					        .generateAliasForTable(this.getCountFunctionInfo().getMainTable());
					
					this.getCountFunctionInfo().getMainTable().setRelatedEtlConfig(this.getEtlConfig());
				}
			}
			
			this.setSqlFunctionLoaded(true);
		}
	}
	
	boolean hasQueryParams() {
		return utilities.listHasElement(this.getQueryParams());
	}
	
	Object getParamValueFromEtlConfig(String param) throws ForbiddenOperationException {
		if (this.getEtlConfig() == null)
			throw new ForbiddenOperationException("The configuration object is not defined");
		
		Object paramValue = this.getEtlConfig().getParamValue(param);
		
		if (paramValue == null) {
			throw new MissingParameterException(param);
		}
		
		return paramValue;
	}
	
	/**
	 * Extract all the parameters presents in a dump query. This assume that the parameter will
	 * start with @
	 * 
	 * @param sqlQuery the query to extract from
	 * @return the list of extracted parameters name
	 */
	private List<QueryParameter> extractParamOnQuery(String sqlQuery) {
		
		logTrace("Extracting query parameters:\n-----------------\n" + sqlQuery + "\n---------------------");
		
		List<QueryParameter> parameters = new ArrayList<>();
		
		List<String> avaliableSubQueries = SQLUtilities.tryToSplitQueryByUnions(sqlQuery);
		
		for (String subQuery : avaliableSubQueries) {
			List<QueryParameter> parametersInSubQuery = extractQueryParametersInSubQuery(subQuery);
			
			if (utilities.listHasElement(parametersInSubQuery)) {
				parameters.addAll(parametersInSubQuery);
			}
		}
		
		return parameters;
	}
	
	private String ensureDynamicElementsLoadedAsParameteres(String query) {
		if (hasDynamicElements()) {
			for (String element : this.getDataSource().getDynamicElements()) {
				query = query.replaceAll(element, "@(" + element + ")");
			}
		}
		
		return query;
	}
	
	private String autoDefineDataSourceParameters(String query) {
		
		if (query == null || query.isBlank()) {
			return query;
		}
		
		if (!this.hasAvaliableDataSources()) {
			return query;
		}
		
		Map<String, Set<String>> dataSourceFields = buildDataSourceFieldsMap(avaliableDataSources);
		
		Pattern pattern = Pattern.compile("\\b([a-zA-Z_][a-zA-Z0-9_]*)\\.([a-zA-Z_][a-zA-Z0-9_]*)\\b");
		
		Matcher matcher = pattern.matcher(query);
		
		StringBuffer sb = new StringBuffer();
		
		while (matcher.find()) {
			
			String dataSourceName = matcher.group(1);
			String fieldName = matcher.group(2);
			String fullToken = matcher.group(0);
			
			Set<String> fields = dataSourceFields.get(dataSourceName);
			
			if (fields != null && fields.contains(fieldName)) {
				matcher.appendReplacement(sb, Matcher.quoteReplacement("@(" + fullToken + ")"));
			} else {
				matcher.appendReplacement(sb, Matcher.quoteReplacement(fullToken));
			}
		}
		
		matcher.appendTail(sb);
		
		return sb.toString();
	}
	
	private Map<String, Set<String>> buildDataSourceFieldsMap(List<EtlDataSource> avaliableDataSources) {
		
		Map<String, Set<String>> result = new HashMap<>();
		
		for (EtlDataSource ds : avaliableDataSources) {
			
			if (ds == null || ds.getName() == null || ds.getFields() == null) {
				continue;
			}
			
			Set<String> fieldNames = new HashSet<>();
			
			for (Field field : ds.getFields()) {
				
				if (field == null || field.getName() == null) {
					continue;
				}
				
				fieldNames.add(field.getName());
			}
			
			result.put(ds.getName(), fieldNames);
		}
		
		return result;
	}
	
	private List<QueryParameter> extractQueryParametersInSubQuery(String subQuery) {
		
		List<QueryParameter> parameters = new ArrayList<>();
		
		Pattern pattern = Pattern.compile("@\\s*(\\w+)|@\\s*\\(");
		Matcher matcher = pattern.matcher(subQuery);
		
		int minAllowedParamStart = 0;
		
		while (matcher.find()) {
			
			int paramStart = matcher.start();
			
			if (paramStart < minAllowedParamStart) {
				continue;
			}
			
			String paramName;
			int paramEnd;
			
			// 🔥 CASO 1: parâmetro composto @(....)
			if (matcher.group().contains("(")) {
				
				int openParenIndex = subQuery.indexOf("(", matcher.start());
				
				String fullParam = extractCompositeParameter(subQuery, openParenIndex);
				
				paramName = fullParam; // mantém expressão inteira
				paramEnd = openParenIndex + fullParam.length();
				
				logTrace("Found COMPOSITE parameter: " + paramName);
				
			}
			// 🔥 CASO 2: parâmetro simples
			else {
				
				paramName = matcher.group(1);
				paramEnd = matcher.end();
				
				logTrace("Found parameter: " + paramName);
			}
			
			QueryParameter params = new QueryParameter(paramName);
			
			logTrace("Trying to extract subquery from starting position " + paramStart + "\nOn Query\n--------------\n"
			        + subQuery);
			
			String containgSubquery = tryToExtractParameterContaingSubQuery(subQuery, paramStart, this.dbmsType);
			
			if (utilities.stringHasValue(containgSubquery)) {
				
				logTrace("Found subquery within the parameter " + params.getName());
				
				int paramStartInSubQuery = determineFirstParameterPositionInQuery(containgSubquery);
				
				int subQueryStart = paramStart - paramStartInSubQuery;
				
				minAllowedParamStart = subQueryStart + containgSubquery.length();
				
				List<QueryParameter> subqueryParams = extractParamOnQuery(containgSubquery);
				
				if (utilities.listHasElement(subqueryParams)) {
					parameters.addAll(subqueryParams);
				}
				
			} else {
				
				logTrace("No subquery found within the parameter on the current query");
				
				minAllowedParamStart = paramStart;
				
				params.determineParameterContext(subQuery, paramStart, paramEnd, this.dbmsType);
				
				logTrace("Context for " + paramName + " is " + params.getContextType());
				
				parameters.add(params);
			}
		}
		
		return parameters;
	}
	
	private String extractCompositeParameter(String query, int startIndex) {
		
		int open = 0;
		int i = startIndex;
		
		for (; i < query.length(); i++) {
			char c = query.charAt(i);
			
			if (c == '(')
				open++;
			else if (c == ')')
				open--;
			
			if (open == 0) {
				return query.substring(startIndex, i + 1);
			}
		}
		
		throw new EtlExceptionImpl("Unclosed composite parameter starting at position " + startIndex);
	}
	
	private int determineFirstParameterPositionInQuery(String sqlQuery) {
		
		Pattern pattern = Pattern.compile("@\\s*(\\w+)|@\\s*\\(");
		Matcher matcher = pattern.matcher(sqlQuery);
		
		if (matcher.find()) {
			return matcher.start();
		}
		
		throw new ForbiddenOperationException("The query does not contain parameters");
	}
	
	private static String tryToExtractParameterContaingSubQuery(String sqlQuery, int paramStart, DbmsType dbmsType) {
		String subQuery = "";
		
		if (paramStart == 533) {
			System.out.println();
		}
		
		Stack<Integer> parenthesisStack = new Stack<>();
		
		boolean foundPossibleSubQueryStarting = false;
		boolean foundPossibleSubQueryFinishing = false;
		
		for (int i = paramStart; i > 0; i--) {
			char currChar = sqlQuery.charAt(i);
			
			if (currChar == '(') {
				//Found the possible starting sub query
				
				if (parenthesisStack.size() == 0) {
					foundPossibleSubQueryStarting = true;
					break;
				} else {
					parenthesisStack.pop();
					subQuery = ("" + currChar) + subQuery;
				}
			} else if (currChar == ')') {
				parenthesisStack.push(i);
				
				subQuery = ("" + currChar) + subQuery;
			} else {
				subQuery = ("" + currChar) + subQuery;
			}
		}
		
		if (foundPossibleSubQueryStarting) {
			for (int i = paramStart + 1; i < sqlQuery.length(); i++) {
				char currChar = sqlQuery.charAt(i);
				
				if (currChar == ')') {
					if (parenthesisStack.size() == 0) {
						foundPossibleSubQueryFinishing = true;
						break;
					} else {
						parenthesisStack.pop();
						subQuery = subQuery + ("" + currChar);
					}
				} else if (currChar == '(') {
					parenthesisStack.push(i);
					
					subQuery = subQuery + ("" + currChar);
					
				} else {
					subQuery = subQuery + currChar;
				}
			}
			
			if (foundPossibleSubQueryFinishing) {
				if (SQLUtilities.isValidSelectSqlQuery(subQuery, dbmsType)) {
					return subQuery;
				}
			}
		}
		
		return null;
		
	}
	
	public static String replaceSqlParametersWithQuestionMarks(String sqlQuery) {
		
		if (sqlQuery == null || sqlQuery.isBlank()) {
			return sqlQuery;
		}
		
		StringBuilder result = new StringBuilder();
		
		int i = 0;
		int length = sqlQuery.length();
		
		while (i < length) {
			
			char c = sqlQuery.charAt(i);
			
			// 🔹 detectar início de parâmetro
			if (c == '@') {
				
				//int start = i;
				
				i++; // skip '@'
				
				// 🔥 ignorar espaços
				while (i < length && Character.isWhitespace(sqlQuery.charAt(i))) {
					i++;
				}
				
				// 🔥 CASO 1: parâmetro composto @(....)
				if (i < length && sqlQuery.charAt(i) == '(') {
					
					int open = 0;
					
					do {
						char ch = sqlQuery.charAt(i);
						
						if (ch == '(')
							open++;
						else if (ch == ')')
							open--;
						
						i++;
						
					} while (i < length && open > 0);
					
					// substituir tudo por ?
					result.append("?");
					
				}
				// 🔥 CASO 2: parâmetro simples
				else {
					
					while (i < length && (Character.isLetterOrDigit(sqlQuery.charAt(i)) || sqlQuery.charAt(i) == '_')) {
						i++;
					}
					
					result.append("?");
				}
				
			} else {
				result.append(c);
				i++;
			}
		}
		
		return result.toString();
	}
	
	public boolean isCountQuery() {
		return this.getCountFunctionInfo() != null;
	}
	
	public IntervalExtremeRecord detemineLimits(Engine<? extends EtlDatabaseObject> engine, Connection conn)
	        throws DBException {
		if (!this.isCountQuery()) {
			throw new ForbiddenOperationException("The query does not use count function!");
		}
		
		return this.getCountFunctionInfo().detemineLimits(engine, conn);
	}
	
	@SuppressWarnings("unchecked")
	public List<EtlDatabaseObject> query(EtlProcessor processor, EtlDatabaseObject srcObject, EtlDatabaseObject dstObject,
	        List<EtlDatabaseObject> srcObjects, Connection conn) throws DBException {
		
		if (this.isCountQuery()) {
			IntervalExtremeRecord limits = this.detemineLimits(processor.getEngine(), conn);
			
			PreparedCountQuerySearchParams searchParams = new PreparedCountQuerySearchParams(this, limits);
			
			long count = searchParams.countAllRecords(limits.getMinRecordId(), limits.getMaxRecordId(), conn);
			
			EtlDatabaseObject obj = this.getDataSource().newInstance();
			obj.setRelatedConfiguration(this.getDataSource());
			
			obj.setFieldValue(this.getCountFunctionInfo().getAliasName(), count);
			
			return utilities.parseToList(obj);
		}
		
		PreparedQueryInfo pq = generatePreparedQuery(processor, srcObject, dstObject, srcObjects, mainQuery, conn);
		
		List<Object> paramsAsList = pq.getParameters();
		
		Object[] params = paramsAsList != null ? paramsAsList.toArray() : null;
		
		return (List<EtlDatabaseObject>) DatabaseObjectDAO.search(this.getDataSource().getLoadHealper(),
		    this.getDataSource().getSyncRecordClass(), pq.getQuery(), params, conn);
	}
	
	@Override
	public String toString() {
		return this.mainQuery;
	}
	
}

class MaintableAliasGenerator implements TableAliasesGenerator {
	
	PreparedQuery pq;
	
	String mainTableName;
	
	public MaintableAliasGenerator(PreparedQuery pq, String mainTableName) {
		this.pq = pq;
		this.mainTableName = mainTableName;
	}
	
	@Override
	public void generateAliasForTable(TableConfiguration tabConfig) {
		String alias = SQLUtilities.extractFirstTableAliasOnSqlQuery(this.pq.getQuery());
		
		if (alias != null) {
			tabConfig.setTableAlias(alias);
		} else {
			tabConfig.setTableAlias(mainTableName);
		}
	}
}
