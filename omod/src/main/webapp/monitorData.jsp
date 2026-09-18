
<%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:htmlInclude file="/moduleResources/epts-etl/scripts/jquery.min.js"/>

<%@ page contentType="text/html; charset=UTF-8" %>

<%
    pageContext.setAttribute("moduleName", "epts-etl");
%>

<!DOCTYPE html>

<html>
<head>

    <title>SIS-RME: Consola de Sincronização de Dados</title>

    <style>

        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            padding: 0;
            font-family: Arial, Helvetica, sans-serif;
            background: #f4f6f8;
            color: #444;
            font-size: 13px;
        }

        .page {
            min-height: 100vh;
        }

        /* =========================================================
           HEADER
           ========================================================= */

        .header {
            height: 48px;
            background: #5b9bd5;
            color: #ffffff;
            display: flex;
            align-items: center;
            padding: 0 20px;
            font-size: 17px;
            font-weight: bold;
        }

        .header-title {
            letter-spacing: 0.2px;
        }

        /* =========================================================
           CONTENT
           ========================================================= */

        .content {
            display: flex;
            padding: 14px;
            gap: 14px;
        }

        /* =========================================================
           SIDEBAR
           ========================================================= */

        .sidebar {
            width: 180px;
            min-width: 180px;
        }

        .status-panel {
            background: #ffffff;
            border: 1px solid #e1e1e1;
            border-radius: 6px;
            padding: 12px;
            margin-bottom: 14px;
        }

        .status-title {
            font-weight: bold;
            margin-bottom: 10px;
            color: #555;
        }

        .status-indicator {
            display: flex;
            align-items: center;
            margin-bottom: 12px;
            font-size: 13px;
        }

        .status-dot {
            width: 9px;
            height: 9px;
            border-radius: 50%;
            background: #999999;
            margin-right: 7px;
        }

        .status-dot.running {
            background: #5b9bd5;
        }

        .status-dot.completed {
            background: #70ad47;
        }

        .status-dot.error {
            background: #d9534f;
        }

        .status-dot.rejected {
            background: #f0ad4e;
        }

        .status-dot.idle {
            background: #999999;
        }

        .status-text {
            font-weight: bold;
        }

        /* =========================================================
           CONTROL BUTTONS
           ========================================================= */

        .control-button {
            width: 100%;
            height: 32px;
            margin-bottom: 7px;
            border: 1px solid #d5d5d5;
            border-radius: 4px;
            background: #f8f8f8;
            color: #666;
            text-align: left;
            padding: 0 10px;
            cursor: pointer;
            font-size: 12px;
            display: flex;
            align-items: center;
        }

        .control-button:hover {
            background: #eeeeee;
        }

        .control-button:disabled {
            cursor: not-allowed;
            opacity: 0.55;
        }

        .button-icon {
            width: 20px;
            min-width: 20px;
            margin-right: 7px;
            text-align: center;
            font-size: 14px;
            font-weight: bold;
        }

        /* =========================================================
           MAIN
           ========================================================= */

        .main {
            flex: 1;
            min-width: 0;
        }

        /* =========================================================
           SUMMARY
           ========================================================= */

        .summary {
            display: grid;
            grid-template-columns: repeat(4, 1fr);
            gap: 10px;
            margin-bottom: 12px;
        }

        .summary-card {
            background: #ffffff;
            border: 1px solid #e1e1e1;
            border-radius: 5px;
            min-height: 82px;
            padding: 12px;
        }

        .summary-card.pending {
            background: #fffdf3;
        }

        .summary-card.success {
            background: #f0f8ed;
        }

        .summary-card.error {
            background: #fff0f2;
        }

        .summary-card.info {
            background: #f7f9fb;
        }

        .summary-label {
            font-size: 11px;
            color: #777;
            margin-bottom: 8px;
        }

        .summary-value {
            font-size: 22px;
            font-weight: bold;
            color: #555;
        }

        .summary-description {
            margin-top: 4px;
            font-size: 10px;
            color: #999;
        }

        .summary-icon {
            display: inline-block;
            margin-right: 5px;
            font-size: 14px;
            font-weight: bold;
        }

        .icon-success {
            color: #70ad47;
        }

        .icon-error {
            color: #d9534f;
        }

        .icon-rejected {
            color: #f0ad4e;
        }

        .icon-info {
            color: #5b9bd5;
        }

        /* =========================================================
           OPERATION PANEL
           ========================================================= */

        .operation-panel {
            background: #ffffff;
            border: 1px solid #e1e1e1;
            border-radius: 5px;
            padding: 14px;
            margin-bottom: 12px;
        }

        .operation-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 8px;
        }

        .operation-title {
            font-size: 14px;
            font-weight: bold;
            color: #555;
        }

        .operation-status {
            display: inline-flex;
            align-items: center;
            padding: 4px 10px;
            border-radius: 12px;
            font-size: 11px;
            font-weight: bold;
            background: #eeeeee;
            color: #666666;
        }

        .operation-status.running {
            background: #d9edf7;
            color: #31708f;
        }

        .operation-status.completed {
            background: #dff0d8;
            color: #3c763d;
        }

        .operation-status.error {
            background: #f2dede;
            color: #a94442;
        }

        .operation-status.rejected {
            background: #fcf8e3;
            color: #8a6d3b;
        }

        .status-icon {
            margin-right: 5px;
            font-size: 13px;
            font-weight: bold;
        }

        .operation-id {
            color: #888;
            font-size: 11px;
            margin-bottom: 10px;
            word-break: break-all;
        }

        .progress-label {
            display: flex;
            justify-content: space-between;
            margin-bottom: 5px;
            font-size: 11px;
            color: #777;
        }

        .progress-container {
            width: 100%;
            height: 22px;
            background: #eeeeee;
            border-radius: 3px;
            overflow: hidden;
        }

        .progress-bar {
            width: 0%;
            height: 100%;
            background: #5b9bd5;
            transition: width 0.4s ease;
            color: #ffffff;
            text-align: center;
            line-height: 22px;
            font-size: 11px;
            font-weight: bold;
        }

        .message {
            display: none;
            margin-top: 10px;
            padding: 9px;
            border-radius: 4px;
            background: #fff0f0;
            color: #a94442;
            border: 1px solid #f0cccc;
        }

        .message.visible {
            display: block;
        }

        /* =========================================================
           REFRESH CARD
           ========================================================= */

        .refresh-value {
            font-size: 20px;
            font-weight: bold;
            color: #5b9bd5;
        }

        .refresh-last {
            margin-top: 5px;
            font-size: 10px;
            color: #999;
        }

        /* =========================================================
           RESPONSIVE
           ========================================================= */

        @media (max-width: 900px) {

            .summary {
                grid-template-columns: repeat(2, 1fr);
            }

            .sidebar {
                width: 150px;
                min-width: 150px;
            }
        }

        @media (max-width: 650px) {

            .content {
                display: block;
            }

            .sidebar {
                width: 100%;
                margin-bottom: 12px;
            }

            .summary {
                grid-template-columns: 1fr;
            }
        }

    </style>

</head>

<body>

<div class="page">

    <!-- =========================================================
         HEADER
         ========================================================= -->

    <div class="header">

        <div class="header-title">
            SIS-RME: Consola de Sincronização de Dados
        </div>

    </div>


    <!-- =========================================================
         CONTENT
         ========================================================= -->

    <div class="content">

        <!-- =====================================================
             SIDEBAR
             ===================================================== -->

        <div class="sidebar">

            <div class="status-panel">

                <div class="status-title">
                    Estado
                </div>

                <div class="status-indicator">

                    <span id="statusDot"
                          class="status-dot idle">
                    </span>

                    <span id="statusText"
                          class="status-text">
                        Inativo
                    </span>

                </div>


                <button id="startButton"
                        class="control-button"
                        type="button"
                        onclick="startEtl(); return false;">

                    <span class="button-icon">▶</span>

                    <span>
                        Iniciar
                    </span>

                </button>


                <button class="control-button"
                        type="button"
                        onclick="showNotImplemented('Pausar')">

                    <span class="button-icon">Ⅱ</span>

                    <span>
                        Pausar
                    </span>

                </button>


                <button id="restartButton"
                        class="control-button"
                        type="button"
                        onclick="restartEtl(); return false;">

                    <span class="button-icon">↻</span>

                    <span>
                        Reiniciar
                    </span>

                </button>


                <button class="control-button"
                        type="button"
                        onclick="showNotImplemented('Reprocessar')">

                    <span class="button-icon">↻</span>

                    <span>
                        Reprocessar
                    </span>

                </button>

            </div>

        </div>


        <!-- =====================================================
             MAIN
             ===================================================== -->

        <div class="main">

            <!-- =================================================
                 SUMMARY
                 ================================================= -->

            <div class="summary">

                <div class="summary-card pending">

                    <div class="summary-label">
                        Mensagens pendentes
                    </div>

                    <div id="pending"
                         class="summary-value">
                        0
                    </div>

                    <div class="summary-description">
                        Aguardando sincronização
                    </div>

                </div>


                <div class="summary-card success">

                    <div class="summary-label">

                        <span class="summary-icon icon-success">
                            ✓
                        </span>

                        Mensagens sincronizadas

                    </div>

                    <div id="processed"
                         class="summary-value">
                        0
                    </div>

                    <div class="summary-description">
                        Processadas com sucesso
                    </div>

                </div>


                <div class="summary-card error">

                    <div class="summary-label">

                        <span class="summary-icon icon-error">
                            ✕
                        </span>

                        Mensagens com erros

                    </div>

                    <div id="errors"
                         class="summary-value">
                        0
                    </div>

                    <div class="summary-description">
                        Erros reportados pelo ETL
                    </div>

                </div>


                <div class="summary-card info">

                    <div class="summary-label">

                        <span class="summary-icon icon-info">
                            ↻
                        </span>

                        Actualização automática

                    </div>

                    <div class="refresh-value">
                        2 segundos
                    </div>

                    <div class="refresh-last">

                        Última actualização:

                        <span id="lastRefresh">
                            -
                        </span>

                    </div>

                </div>

            </div>


            <!-- =================================================
                 OPERATION
                 ================================================= -->

            <div class="operation-panel">

                <div class="operation-header">

                    <div class="operation-title">
                        Sincronização actual
                    </div>

                    <div id="operationStatus"
                         class="operation-status">

                        <span class="status-icon">
                            ●
                        </span>

                        <span id="operationStatusText">
                            IDLE
                        </span>

                    </div>

                </div>


                <div class="operation-id">

                    Operação:

                    <span id="operationId">
                        -
                    </span>

                </div>


                <div class="progress-label">

                    <span>
                        Progresso
                    </span>

                    <span id="progressText">
                        0%
                    </span>

                </div>


                <div class="progress-container">

                    <div id="progressBar"
                         class="progress-bar">
                        0%
                    </div>

                </div>


                <div id="message"
                     class="message">
                </div>

            </div>

        </div>

    </div>

</div>


<script>

    var monitorTimer = null;


    /* =========================================================
       STATUS CLASS
       ========================================================= */

    function getStatusClass(status) {

        if (!status) {
            return "idle";
        }

        status = String(status).toUpperCase();

        if (status === "RUNNING") {
            return "running";
        }

        if (status === "COMPLETED" ||
            status === "SUCCESS" ||
            status === "FINISHED") {

            return "completed";
        }

        if (status === "ERROR" ||
            status === "FAILED") {

            return "error";
        }

        if (status === "REJECTED" ||
            status === "REJECT") {

            return "rejected";
        }

        return "idle";
    }


    /* =========================================================
       STATUS LABEL
       ========================================================= */

    function getStatusLabel(status) {

        if (!status) {
            return "Inativo";
        }

        status = String(status).toUpperCase();

        if (status === "RUNNING") {
            return "Activo";
        }

        if (status === "COMPLETED" ||
            status === "SUCCESS" ||
            status === "FINISHED") {

            return "Sucesso";
        }

        if (status === "ERROR" ||
            status === "FAILED") {

            return "Falhou";
        }

        if (status === "REJECTED" ||
            status === "REJECT") {

            return "Rejeitado";
        }

        if (status === "IDLE") {
            return "Inativo";
        }

        if (status === "INITIALIZED") {
            return "Inicializado";
        }

        if (status === "NOT_INITIALIZED") {
            return "Não inicializado";
        }

        if (status === "DISABLED") {
            return "Desactivado";
        }

        if (status === "STOPPED") {
            return "Parado";
        }

        return status;
    }


    /* =========================================================
       STATUS ICON
       ========================================================= */

    function getStatusIcon(status) {

        if (!status) {
            return "●";
        }

        status = String(status).toUpperCase();

        if (status === "RUNNING") {
            return "●";
        }

        if (status === "COMPLETED" ||
            status === "SUCCESS" ||
            status === "FINISHED") {

            return "✓";
        }

        if (status === "ERROR" ||
            status === "FAILED") {

            return "✕";
        }

        if (status === "REJECTED" ||
            status === "REJECT") {

            return "⊘";
        }

        return "●";
    }


    /* =========================================================
       NUMBER
       ========================================================= */

    function formatNumber(value) {

        if (value === null ||
            value === undefined ||
            isNaN(Number(value))) {

            return "0";
        }

        return Number(value).toLocaleString();
    }


    /* =========================================================
       MAIN UPDATE
       ========================================================= */

    function updateMonitor(data) {

        if (!data) {
            return;
        }

        var status =
            data.status || "IDLE";

        var statusClass =
            getStatusClass(status);


        /* -----------------------------------------------------
           SIDEBAR STATUS
           ----------------------------------------------------- */

        var statusDot =
            document.getElementById("statusDot");

        statusDot.className =
            "status-dot " + statusClass;


        document.getElementById("statusText").innerText =
            getStatusLabel(status);


        /* -----------------------------------------------------
           OPERATION
           ----------------------------------------------------- */

        document.getElementById("operationId").innerText =
            data.operationId || "-";


        var operationStatus =
            document.getElementById("operationStatus");


        operationStatus.className =
            "operation-status " + statusClass;


        document.getElementById("operationStatusText").innerText =
            getStatusLabel(status);


        var statusIcon =
            operationStatus.querySelector(".status-icon");


        if (statusIcon) {

            statusIcon.innerText =
                getStatusIcon(status);

        }


        /* -----------------------------------------------------
           PROCESSED
           ----------------------------------------------------- */

        var processed =
            Number(data.processed || 0);


        var total =
            Number(data.total || 0);


        document.getElementById("processed").innerText =
            formatNumber(processed);


        /* -----------------------------------------------------
           PENDING
           ----------------------------------------------------- */

        var pending =
            Math.max(
                0,
                total - processed
            );


        document.getElementById("pending").innerText =
            formatNumber(pending);


        /* -----------------------------------------------------
           PROGRESS
           ----------------------------------------------------- */

        var progress =
            Number(data.progress || 0);


        progress =
            Math.max(
                0,
                Math.min(
                    100,
                    progress
                )
            );


        var progressBar =
            document.getElementById("progressBar");


        progressBar.style.width =
            progress + "%";


        progressBar.innerText =
            Math.round(progress) + "%";


        document.getElementById("progressText").innerText =
            progress.toFixed(2) + "%";


        /* -----------------------------------------------------
           ERRORS

           Nesta release mantemos apenas o contador.
           A visualização detalhada será implementada
           numa release futura, depois de definirmos como
           tratar os message_uuid e os payloads de erro.
           ----------------------------------------------------- */

        var errorCount =
            0;


        if (data.tasks &&
            data.tasks.length) {

            for (var i = 0;
                 i < data.tasks.length;
                 i++) {

                var task =
                    data.tasks[i];


                if (task &&
                    task.status &&
                    (
                        String(task.status).toUpperCase() === "ERROR" ||
                        String(task.status).toUpperCase() === "FAILED"
                    )) {

                    errorCount++;

                }

            }

        }


        document.getElementById("errors").innerText =
            formatNumber(errorCount);


        /* -----------------------------------------------------
           MESSAGE
           ----------------------------------------------------- */

        var messageElement =
            document.getElementById("message");


        if (data.message) {

            messageElement.innerText =
                data.message;

            messageElement.className =
                "message visible";

        } else {

            messageElement.innerText =
                "";

            messageElement.className =
                "message";

        }


        /* -----------------------------------------------------
           REFRESH TIME
           ----------------------------------------------------- */

        document.getElementById("lastRefresh").innerText =
            new Date().toLocaleTimeString();

    }


    /* =========================================================
       INICIAR ETL
       ========================================================= */

    function startEtl() {

        var button =
            document.getElementById("startButton");


        if (button) {
            button.disabled = true;
        }


        fetch(
            "${pageContext.request.contextPath}/module/epts/etl/start.form",
            {
                method: "POST",
                headers: {
                    "Accept": "application/json"
                },
                cache: "no-cache"
            }
        )
            .then(function(response) {

                if (!response.ok) {
                    throw new Error(
                        "HTTP " + response.status
                    );
                }

                return response.json();

            })
            .then(function(data) {

                console.log(
                    "[ETL-START]",
                    data
                );


                if (data.message) {

                    var messageElement =
                        document.getElementById("message");


                    messageElement.innerText =
                        data.message;


                    messageElement.className =
                        "message visible";
                }


                /*
                 * Não mudamos de página.
                 *
                 * Apenas pedimos imediatamente o estado
                 * actualizado ao monitor.
                 */

                loadMonitor();

            })
            .catch(function(error) {

                console.error(
                    "[ETL-START] Error:",
                    error
                );


                var messageElement =
                    document.getElementById("message");


                messageElement.innerText =
                    "Não foi possível iniciar o ETL: " +
                    error.message;


                messageElement.className =
                    "message visible";

            })
            .finally(function() {

                if (button) {
                    button.disabled = false;
                }

            });

    }


    /* =========================================================
       REINICIAR ETL
       ========================================================= */

    function restartEtl() {

        if (!confirm(
            "Tem certeza que deseja reiniciar o ETL?"
        )) {

            return;
        }


        var button =
            document.getElementById("restartButton");


        if (button) {
            button.disabled = true;
        }


        fetch(
            "${pageContext.request.contextPath}/module/epts/etl/restart.form",
            {
                method: "POST",
                headers: {
                    "Accept": "application/json"
                },
                cache: "no-cache"
            }
        )
            .then(function(response) {

                if (!response.ok) {

                    throw new Error(
                        "HTTP " + response.status
                    );

                }

                return response.json();

            })
            .then(function(data) {

                console.log(
                    "[ETL-RESTART]",
                    data
                );


                if (data.message) {

                    var messageElement =
                        document.getElementById("message");


                    messageElement.innerText =
                        data.message;


                    messageElement.className =
                        "message visible";
                }


                /*
                 * Continua na mesma página.
                 *
                 * O monitor continua a actualizar
                 * automaticamente a cada 2 segundos.
                 */

                loadMonitor();

            })
            .catch(function(error) {

                console.error(
                    "[ETL-RESTART] Error:",
                    error
                );


                var messageElement =
                    document.getElementById("message");


                messageElement.innerText =
                    "Não foi possível reiniciar o ETL: " +
                    error.message;


                messageElement.className =
                    "message visible";

            })
            .finally(function() {

                if (button) {
                    button.disabled = false;
                }

            });

    }


    /* =========================================================
       MONITOR API
       ========================================================= */

    function loadMonitor() {

        fetch(
            "${pageContext.request.contextPath}/module/epts/etl/monitorData.json",
            {
                method: "GET",
                headers: {
                    "Accept": "application/json"
                },
                cache: "no-cache"
            }
        )
            .then(function(response) {

                if (!response.ok) {

                    throw new Error(
                        "HTTP " + response.status
                    );

                }

                return response.json();

            })
            .then(function(data) {

                console.log(
                    "[ETL-MONITOR] Data received:",
                    data
                );


                updateMonitor(data);

            })
            .catch(function(error) {

                console.error(
                    "[ETL-MONITOR] Error:",
                    error
                );


                var messageElement =
                    document.getElementById("message");


                messageElement.innerText =
                    "Não foi possível obter informações do monitor: "
                    + error.message;


                messageElement.className =
                    "message visible";


                document.getElementById(
                    "statusText"
                ).innerText =
                    "Erro";


                document.getElementById(
                    "statusDot"
                ).className =
                    "status-dot error";


                document.getElementById(
                    "operationStatus"
                ).className =
                    "operation-status error";


                document.getElementById(
                    "operationStatusText"
                ).innerText =
                    "Falhou";


                var statusIcon =
                    document.querySelector(
                        "#operationStatus .status-icon"
                    );


                if (statusIcon) {

                    statusIcon.innerText =
                        "✕";

                }

            });

    }


    /* =========================================================
       TEMPORARY BUTTON HANDLER
       ========================================================= */

    function showNotImplemented(action) {

        alert(
            action +
            " ainda não está ligado a um endpoint de controlo do ETL."
        );

    }


    /* =========================================================
       START
       ========================================================= */

    loadMonitor();


    monitorTimer =
        setInterval(
            loadMonitor,
            2000
        );

</script>

</body>
</html>