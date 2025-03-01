<%@ page import="java.io.File" %>
<%
    String pageContent = (String) request.getAttribute("pageContent");
    if (pageContent != null && !pageContent.isEmpty()) {
        // Ajouter "WEB-INF/" devant le nom de la pageContent pour rechercher dans le bon répertoire
        String pageContentPath = "/WEB-INF/views/" + pageContent + ".jsp";
        File file = new File(application.getRealPath(pageContentPath));
        
        // Vérifier si le fichier existe dans WEB-INF/
        if (file.exists() && file.isFile()) {
            // Si le fichier existe, inclure la pageContent
            request.setAttribute("pageContent", pageContentPath);
            request.getRequestDispatcher(pageContentPath).include(request, response);
        } else {
            // Si le fichier n'existe pas, retourner un 404
            %>
            <script>
            alert("Page not found: <%=pageContent%>");
            </script>
            <%
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    } else {
        // Si aucun contenu n'est spécifié, retourner un 404
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
%>
