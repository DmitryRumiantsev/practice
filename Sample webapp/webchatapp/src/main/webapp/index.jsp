<html>
<body>
<h2>Hello World!</h2>
<%
final String[] vars = { "JAVA_HOME", "M2_HOME","CATALINA_HOME","USERNAME","PATH"};
for (String var : vars) {
            out.println(String.format("%s=%s", var, System.getenv(var)));
        }
%>
</body>
</html>
