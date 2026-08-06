<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/epts-etl/css/epts-etl.css" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="template/localHeader.jsp"%>


<script src="https://code.jquery.com/jquery-1.11.3.min.js"></script>


<style>

    .progress {
        height: 25px;
        width: 100%;
        background-color: #ddd;
        position: relative;
    }


    .progress .value {

        background-color: #4CAF50;
        height: 100%;
        width:0%;

    }


    .progress span {

        position:absolute;
        width:100%;
        text-align:center;
        line-height:25px;
        color:black;

    }


    table {

        width:100%;
        border-collapse: collapse;

    }


    table th,
    table td {

        border:1px solid #ddd;
        padding:8px;

    }

</style>



<script type="text/javascript">


    window.setInterval(
        refreshMonitor,
        5000
    );



    function refreshMonitor(){


        $.ajax({

            url:
                '${pageContext.request.contextPath}/module/epts/etl/monitorData.form',


            type:'GET',


            dataType:'json',



            success:function(data){



                $('#operationId')
                    .html(data.operationId);



                $('#status')
                    .html(data.status);



                $('#progressText')
                    .html(
                        data.progress + '%'
                    );



                $('#progressBar')
                    .css(
                        'width',
                        data.progress + '%'
                    );



                $('#processed')
                    .html(
                        data.processed
                        +
                        '/'
                        +
                        data.total
                    );



                /*
                 * Tasks
                 */

                var rows='';


                if(data.tasks){


                    data.tasks.forEach(function(task){


                        rows += '<tr>';

                        rows += '<td>'
                            + task.processorId
                            +'</td>';


                        rows += '<td>'
                            + task.status
                            +'</td>';


                        rows += '<td>'
                            + (task.minRecord ?? '')
                            +'</td>';


                        rows += '<td>'
                            + (task.maxRecord ?? '')
                            +'</td>';


                        rows += '</tr>';


                    });


                }



                $('#tasks tbody')
                    .html(rows);



            },


            error:function(){

                console.log(
                    "Monitor unavailable"
                );

            }



        });


    }



</script>




<h2>
    ETL Sync Monitor
</h2>



<br>



<fieldset>


    <legend>
        Execution Status
    </legend>


    <table>


        <tr>

            <td>
                Operation
            </td>

            <td id="operationId">
                -
            </td>

        </tr>



        <tr>

            <td>
                Status
            </td>

            <td id="status">
                -
            </td>

        </tr>



        <tr>

            <td>
                Progress
            </td>


            <td>


                <div class="progress">


<span id="progressText">
0%
</span>


                    <div
                            id="progressBar"
                            class="value">
                    </div>


                </div>


            </td>


        </tr>



        <tr>


            <td>
                Records
            </td>


            <td id="processed">
                -
            </td>


        </tr>



    </table>


</fieldset>




<br>



<fieldset>


    <legend>
        Active Processors
    </legend>



    <table id="tasks">


        <thead>


        <tr>

            <th>
                Processor
            </th>


            <th>
                Status
            </th>


            <th>
                Min Record
            </th>


            <th>
                Max Record
            </th>


        </tr>


        </thead>


        <tbody>

        </tbody>


    </table>



</fieldset>



<br>



<input
        type="button"
        value="Refresh"
        onclick="refreshMonitor()"
/>



<%@ include file="/WEB-INF/template/footer.jsp"%>