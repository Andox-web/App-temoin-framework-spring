<%@ page import="java.util.List" %>
<%@ page import="mg.etu2624.ticketing.model.Vol" %>
<%@ page import="mg.etu2624.ticketing.model.ClasseSiege" %>
<%@ page import="mg.etu2624.ticketing.model.Promotion" %>
<%@ page import="mg.itu.prom16.validation.BindingResult" %>
<%@ page import="mg.itu.prom16.validation.Field" %>
<%
    List<Vol> vols = (List<Vol>) request.getAttribute("vols");
    List<ClasseSiege> classes = (List<ClasseSiege>) request.getAttribute("classes");
    BindingResult result = (BindingResult) request.getAttribute("value");
    boolean isResult = result != null && result.hasErrors();
%>
<!DOCTYPE html>
<html>
<head>
    <title>Gestion Promotions</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/resources/assets/css/form.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/resources/assets/css/promotion.css">
</head>
<body>
    <div class="form-container">
        <h2>Nouvelle Promotion</h2>
        
        <!-- Select Vol -->
        <div class="form-group">
            <label>Vol :</label>
            <select id="selectVol" onchange="loadPromotions(this.value)" class="full-width">
                <option value="">-- Choisir --</option>
                <% for (Vol vol : vols) { 
                    String selected = "";
                    if (isResult && result.getErrors("volId") != null) {
                        String submittedVolId = result.getErrors("volId").getFieldValueAsString();
                        if (submittedVolId.equals(String.valueOf(vol.getId()))) {
                            selected = "selected";
                        }
                    }
                %>
                    <option value="<%= vol.getId() %>" <%= selected %>>
                        Vol <%= vol.getNumeroVol() %> - <%= vol.getOrigine() %> &rarr; <%= vol.getDestination() %>
                    </option>
                <% } %>
            </select>
            <% if (isResult && result.getErrors("volId") != null) { %>
                <span class="error-message"><%= result.getErrors("volId").getFirstError() %></span>
            <% } %>
        </div>

        <!-- Liste promotions existantes -->
        <div id="promotionsList" class="promotions-container">
            <% List<Promotion> promotions = (List<Promotion>) request.getAttribute("promotions");
               if (promotions != null) { 
                   for (Promotion p : promotions) { %>
                    <div class="promotion-item">
                        <div class="promotion-header">
                            <span><%= p.getClasseSiege().getNom() %></span>
                            <span>-<%= p.getPourcentageReduction() %>%</span>
                        </div>
                        <div class="promotion-details">
                            <span>Utilisé : <%= p.getPromotionUtilisee() %>/<%= p.getLimiteSieges() %></span>
                            <span><%= p.getDescription() %></span>
                        </div>
                    </div>
            <%   }
               } %>
        </div>

        <!-- Formulaire -->
        <form id="promotionForm" action="<%=request.getContextPath()%>/admin/promotions" method="post">
            <div class="form-grid">
                <div class="form-group">
                    <label>Description :</label>
                    <input type="text" name="description" 
                           value='<%= isResult && result.getErrors("description") != null ? result.getErrors("description").getFieldValueAsString() : "" %>' 
                           required>
                    <% if (isResult && result.getErrors("description") != null) { %>
                        <span class="error-message"><%= result.getErrors("description").getFirstError() %></span>
                    <% } %>
                </div>
                
                <div class="form-group">
                    <label>Pourcentage :</label>
                    <input type="number" name="pourcentageReduction" step="0.01" 
                           min="0.01" max="100" 
                           value='<%= isResult && result.getErrors("pourcentageReduction") != null ? result.getErrors("pourcentageReduction").getFieldValueAsString() : "" %>' 
                           required>
                    <% if (isResult && result.getErrors("pourcentageReduction") != null) { %>
                        <span class="error-message"><%= result.getErrors("pourcentageReduction").getFirstError() %></span>
                    <% } %>
                </div>
                
                <div class="form-group">
                    <label>Classe :</label>
                    <select name="classeSiegeId" required>
                        <option value="">-- Choisir --</option>
                        <% for (ClasseSiege classe : classes) { 
                            String selected = "";
                            if (isResult && result.getErrors("classeSiegeId") != null) {
                                String submittedValue = result.getErrors("classeSiegeId").getFieldValueAsString();
                                if (submittedValue.equals(String.valueOf(classe.getId()))) {
                                    selected = "selected";
                                }
                            }
                        %>
                            <option value="<%= classe.getId() %>" <%= selected %>>
                                <%= classe.getNom() %>
                            </option>
                        <% } %>
                    </select>
                    <% if (isResult && result.getErrors("classeSiegeId") != null) { %>
                        <span class="error-message"><%= result.getErrors("classeSiegeId").getFirstError() %></span>
                    <% } %>
                </div>
                
                <div class="form-group">
                    <label>Limite :</label>
                    <input type="number" name="limiteSieges" min="1" 
                           value='<%= isResult && result.getErrors("limiteSieges") != null ? result.getErrors("limiteSieges").getFieldValueAsString() : "" %>' 
                           required>
                    <% if (isResult && result.getErrors("limiteSieges") != null) { %>
                        <span class="error-message"><%= result.getErrors("limiteSieges").getFirstError() %></span>
                    <% } %>
                </div>
            </div>
            
            <div class="error-message">
                <%= request.getAttribute("error") != null ? request.getAttribute("error") : "" %>
            </div>
            
            <button type="submit">Valider</button>
        </form>
    </div>

    <script>
    function loadPromotions(volId) {
        if (!volId) {
            // Vider la liste si aucun vol sélectionné
            document.getElementById('promotionsList').innerHTML = '';
            return;
        }
        
        fetch('<%=request.getContextPath()%>/admin/promotions/vols/' + volId)
            .then(function(response) {
                if (!response.ok) {
                    throw new Error('HTTP error! status: ' + response.status);
                }
                return response.json();
            })
            .then(function(promotions) {
                var container = document.getElementById('promotionsList');
                
                if (!promotions || promotions.length === 0) {
                    container.innerHTML = '<div class="no-promotions">Aucune promotion disponible pour ce vol</div>';
                    return;
                }

                var promotionsHTML = '';
                for (var i = 0; i < promotions.length; i++) {
                    var p = promotions[i];
                    
                    // Validation des données
                    var pourcentage = parseFloat(p.pourcentageReduction).toFixed(0);
                    var classeNom = p.classeSiege ? p.classeSiege.nom : 'Classe inconnue';
                    var utilise = parseInt(p.promotionUtilisee) || 0;
                    var limite = parseInt(p.limiteSieges) || 0;
                    var description = p.description || '';

                    promotionsHTML += 
                        '<div class="promotion-item">' +
                            '<div class="promotion-header">' +
                                '<span>' + classeNom + '</span>' +
                                '<span>-' + pourcentage + '%</span>' +
                            '</div>' +
                            '<div class="promotion-details">' +
                                '<span>Utilise : ' + utilise + '/' + limite + '</span>' +
                                '<span>' + description + '</span>' +
                            '</div>' +
                        '</div>';
                }

                container.innerHTML = promotionsHTML;
            })
            .catch(function(error) {
                console.error('Error:', error);
                document.getElementById('promotionsList').innerHTML = 
                    '<div class="error-message">Erreur de chargement des promotions</div>';
            });
    }
    
    document.getElementById('promotionForm').addEventListener('submit', function(e) {
        e.preventDefault();
        const volIdInput = document.createElement('input');
        volIdInput.type = 'hidden';
        volIdInput.name = 'volId';
        volIdInput.value = document.getElementById('selectVol').value;
        this.appendChild(volIdInput);
        this.submit();
    });
    </script>
</body>
</html>