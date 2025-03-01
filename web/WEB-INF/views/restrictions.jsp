<%@ page import="mg.itu.prom16.validation.BindingResult" %>
<%@ page import="java.util.List" %>
<%@ page import="mg.etu2624.ticketing.model.Vol" %>
<%
    BindingResult result = (BindingResult) request.getAttribute("value");
    boolean hasErrors = result != null && result.hasErrors();
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestion des Restrictions</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/resources/assets/css/form.css">
</head>
<body>
    <div class="form-container">
        <h1>Gestion des Restrictions</h1>
        
        <!-- Affichage des messages de succès ou d'erreur -->
        <% if (request.getAttribute("success") != null) { %>
            <div class="success">Les restrictions ont ete sauvegardees avec succes !</div>
        <% } %>
        <% if (request.getAttribute("error") != null) { %>
            <div class="error"><%= request.getAttribute("error") %></div>
        <% } %>

        <!-- Formulaire de saisie -->
        <form method="post">
            <!-- Champ Vol ID (optionnel) -->
            <div class="form-group">
                <label for="volId">Vol ID (optionnel) :</label>
                <select id="volId" name="volId">
                    <option value="">-- Selectionnez un vol --</option>
                    <% 
                    // Recuperer la liste des vols depuis la base de donnees
                    List<Vol> vols = (List<Vol>) request.getAttribute("vols");
                    if (vols != null) {
                        for (Vol vol : vols) {
                            boolean isSelected = (request.getParameter("volId") != null 
                                && request.getParameter("volId").equals(vol.getId().toString()));
                    %>
                    <option value="<%= vol.getId() %>" <%= isSelected ? "selected" : "" %>>
                        Vol <%= vol.getId() %> - <%= vol.getNumeroVol() %>
                    </option>
                    <% 
                        }
                    }
                    %>
                </select>
            </div>

            <!-- Champ Delai creation -->
            <div class="form-group">
                <label for="delaiCreationHeures">Delai creation (heures) :</label>
                <input type="number" step="0.1" id="delaiCreationHeures" name="delaiCreationHeures" 
                       value="${not empty param.delaiCreationHeures ? param.delaiCreationHeures : ''}">
                <% if (hasErrors && result.getErrors("delaiCreationHeures") != null) { %>
                    <span class="error"><%= result.getErrors("delaiCreationHeures").getFirstError() %></span>
                <% } %>
            </div>

            <!-- Champ Delai annulation -->
            <div class="form-group">
                <label for="delaiAnnulationHeures">Delai annulation (heures) :</label>
                <input type="number" step="0.1" id="delaiAnnulationHeures" name="delaiAnnulationHeures" 
                       value="${not empty param.delaiAnnulationHeures ? param.delaiAnnulationHeures : ''}">
                <% if (hasErrors && result.getErrors("delaiAnnulationHeures") != null) { %>
                    <span class="error"><%= result.getErrors("delaiAnnulationHeures").getFirstError() %></span>
                <% } %>
            </div>

            <!-- Bouton de soumission -->
            <button type="submit">Sauvegarder</button>
        </form>
    </div>
</body>
</html>