<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Url Shortener</title>
</head>
<body>

<%
    String fullUrl = request.getParameter("full_url");
    String shortUrl = response.getHeader("short_url");
%>

<form action="${pageContext.request.contextPath}/generate-url" method="post">
    <label for="full_url">Full url:</label><br>
    <input type="text" id="full_url" name="full_url" value=<%=fullUrl == null ? "" : fullUrl%>><br>
    <label for="short_url">Short url:</label><br>
    <input type="text" id="short_url" name="short_url" value=<%=shortUrl == null ? "" : shortUrl%>><br><br>
    <input type="submit" value="Generate short link">
</form>
</body>
</html>