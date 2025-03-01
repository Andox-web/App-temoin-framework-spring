<%@ page import="mg.etu2624.ticketing.model.Vol" %>
<%@ page import="mg.etu2624.ticketing.model.Avion" %>
<%@ page import="mg.etu2624.ticketing.model.ClasseSiege" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="mg.etu2624.ticketing.model.view.OccupationSiege" %>
<%
    List<Vol> vols = (List<Vol>) request.getAttribute("vols");
    List<Avion> avions = (List<Avion>) request.getAttribute("avions");
    List<ClasseSiege> classesSiege = (List<ClasseSiege>) request.getAttribute("classesSiege");
    Map<Long, List<OccupationSiege>> occupationMap = (Map<Long, List<OccupationSiege>>) request.getAttribute("occupationMap");
    boolean isLogged = session.getAttribute("user")!=null;
    

%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Liste des Vols</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/resources/assets/css/list.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/resources/assets/css/form.css">
</head>
<body>
    <div class="table-container">
        <div>
            <a class="list" style="display: inline-block; padding: 10px 20px; font-size: 16px; font-weight: bold; color: var(--color-text-light); background-color: var(--color-sidebar); text-decoration: none; border-radius: 5px; transition: background-color 0.3s, transform 0.2s;">
                Recherche
            </a>
            <div class="submenu">
                <form class="form-container search-form" method="get">
                    <div class="form-grid">
                        <!-- Numéro vol -->
                        <div class="form-group">
                            <label for="numeroVol">Numero vol:</label>
                            <input type="text" id="numeroVol" name="numeroVol" 
                                value="<%= request.getParameter("numeroVol") != null ? request.getParameter("numeroVol") : "" %>">
                        </div>

                        <!-- Avion -->
                        <div class="form-group">
                            <label for="avionId">Avion:</label>
                            <select id="avionId" name="avionId">
                                <option value="">Tous</option>
                                <% for (Avion avion : avions) { 
                                    String selected = request.getParameter("avionId") != null && 
                                        request.getParameter("avionId").equals(String.valueOf(avion.getId())) ? "selected" : "";
                                %>
                                    <option value="<%= avion.getId() %>" <%= selected %>>
                                        <%= avion.getModele() %>
                                    </option>
                                <% } %>
                            </select>
                        </div>

                        <!-- Classe -->
                        <div class="form-group">
                            <label for="classeId">Classe:</label>
                            <select id="classeId" name="classeId">
                                <option value="">Toutes</option>
                                <% for (ClasseSiege classe : classesSiege) { 
                                    String selected = request.getParameter("classeId") != null && 
                                        request.getParameter("classeId").equals(String.valueOf(classe.getId())) ? "selected" : "";
                                %>
                                    <option value="<%= classe.getId() %>" <%= selected %>>
                                        <%= classe.getNom() %>
                                    </option>
                                <% } %>
                            </select>
                        </div>

                        <!-- Plage de dates -->
                        <div class="form-group">
                            <label>Date depart:</label>
                            <div class="date-group">
                                <input type="date" id="departMin" name="departMin" 
                                    value="<%= request.getParameter("departMin") != null ? request.getParameter("departMin") : "" %>">
                                <input type="date" id="departMax" name="departMax"
                                    value="<%= request.getParameter("departMax") != null ? request.getParameter("departMax") : "" %>">
                            </div>
                        </div>

                        <!-- Origine/Destination -->
                        <div class="form-group">
                            <label>Trajet:</label>
                            <div class="trajet-inputs">
                                <input type="text" id="origine" name="origine" placeholder="Origine"
                                    value="<%= request.getParameter("origine") != null ? request.getParameter("origine") : "" %>">
                                <span>&rarr;</span>
                                <input type="text" id="destination" name="destination" placeholder="Destination"
                                    value="<%= request.getParameter("destination") != null ? request.getParameter("destination") : "" %>">
                            </div>
                        </div>

                        <!-- Prix min/max -->
                        <div class="form-group">
                            <label for="prixMin">Prix min:</label>
                            <input type="number" id="prixMin" name="prixMin" step="0.01" 
                                value="<%= request.getParameter("prixMin") != null ? request.getParameter("prixMin") : "" %>">
                        </div>
                        
                        <div class="form-group">
                            <label for="prixMax">Prix max:</label>
                            <input type="number" id="prixMax" name="prixMax" step="0.01"
                                value="<%= request.getParameter("prixMax") != null ? request.getParameter("prixMax") : "" %>">
                        </div>
                    </div>
                    
                    <div class="form-actions">
                        <button type="submit" class="btn-search">Rechercher</button>
                        <a href="<%=request.getContextPath()%>/admin/vols" class="btn-reset">Reinitialiser</a>
                    </div>
                </form>
            </div>
        </div>

        <table>
            <thead>
                <tr>
                    <th>Vol</th>
                    <th>Avion</th>
                    <th>Depart</th>
                    <th>Arrivee</th>
                    <th>Trajet</th>
                    <th>Occupation</th>
                    <%
                        if(isLogged) {
                            %>
                            <th>Actions</th>
                            <%
                        }
                    %>
                </tr>
            </thead>
            <tbody>
                <% for (Vol vol : vols) { %>
                    <tr>
                        <td><a href="<%=request.getContextPath()%>/vols/view/<%= vol.getId() %>"><%= vol.getNumeroVol() %></a></td>
                        <td><%= vol.getAvion().getModele() %></td>
                        <td><%= vol.getDepart() %></td>
                        <td><%= vol.getArrivee() != null ? vol.getArrivee() : "-" %></td>
                        <td><%= vol.getOrigine() %> &rarr; <%= vol.getDestination() %></td>
                        <td>
                            <% if (occupationMap != null && occupationMap.containsKey(vol.getId())) { 
                                for (OccupationSiege occ : occupationMap.get(vol.getId())) { %>
                                    <div class="occupation-line">
                                        <span class="classe"><%= occ.getClasseSiege() %></span>
                                        <span class="stats">
                                            <em><%= occ.getNombreOccupe() %>/<%= occ.getNombreTotalSieges() %></em>
                                            (<%= occ.getNombreLibre() %>)
                                        </span>
                                        <span class="prix"><%= String.format("%,.2f", occ.getPrix()) %> Ar</span>
                                    </div>
                                <% } 
                            } %>
                        </td>
                        <%
                        if(isLogged) {
                            %>
                                <td class="actions">
                                    <a class="btn-editer" href="<%=request.getContextPath()%>/admin/vols/editer/<%= vol.getId() %>">Editer</a>
                                    <a class="btn-supprimer" href="<%=request.getContextPath()%>/admin/vols/supprimer/<%= vol.getId() %>">Supprimer</a>
                                </td>
                            <%
                            }
                        %>
                    </tr>
                <% } %>
            </tbody>
        </table>
    </div>
</body>
</html>