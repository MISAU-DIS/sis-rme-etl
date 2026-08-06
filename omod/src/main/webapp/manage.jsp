<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>

<%@ include file="template/localHeader.jsp"%>


<p>
    Hello ${user.systemId}!
</p>


<br/>


<h3>
    ETL Management
</h3>


<input
        type="button"
        value="Open Sync Monitor"
        onclick="window.location='monitor.form'"
/>


<%@ include file="/WEB-INF/template/footer.jsp"%>