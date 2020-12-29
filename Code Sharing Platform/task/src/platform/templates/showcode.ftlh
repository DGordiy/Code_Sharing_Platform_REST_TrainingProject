<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Code</title>
    <style type="text/css">
        #load_date {color: green}
        #code_snippet {background: lightGray; border-style: solid}
    </style>
</head>
<body>
<#list codes as codeEntity>
<span id="load_date">${codeEntity.formattedLoadDate}</span>
<pre id="code_snippet">
${codeEntity.code}
</pre>
<br>
</#list>
</body>
</html>