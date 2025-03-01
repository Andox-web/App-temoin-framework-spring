<%@ page import="mg.etu2624.ticketing.model.Avion" %>
<%@ page import="mg.etu2624.ticketing.model.ClasseSiege" %>
<%@ page import="java.util.List" %>
<%
    List<Avion> avions = (List<Avion>) request.getAttribute("avions");
    List<ClasseSiege> classesSiege = (List<ClasseSiege>) request.getAttribute("classesSiege");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Creer un Vol</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/resources/assets/css/form.css">
</head>
<body>
    <form class="form-container" id="create-vol-form">
        <!-- Numero Vol -->
        <div class="form-group">
            <label for="numeroVol">Numero de Vol:</label>
            <input type="text" id="numeroVol" name="numeroVol" required>
            <div class="error-message" data-field="numeroVol"></div>
        </div>

        <!-- Avion -->
        <div class="form-group">
            <label for="avionId">Avion:</label>
            <select id="avionId" name="avionId" required>
                <option value="">Selectionnez un avion</option>
                <% for (Avion avion : avions) { %>
                    <option value="<%= avion.getId() %>"><%= avion.getModele() %></option>
                <% } %>
            </select>
            <div class="error-message" data-field="avionId"></div>
        </div>

        <!-- Date Depart -->
        <div class="form-group">
            <label for="depart">Depart:</label>
            <input type="datetime-local" id="depart" name="depart" required>
            <div class="error-message" data-field="depart"></div>
        </div>

        <!-- Duree Estimee -->
        <div class="form-group">
            <label for="dureeEstimee">Duree Estimee:</label>
            <input type="number" id="dureeEstimee" name="dureeEstimee" required step="0.01">
            <div class="error-message" data-field="dureeEstimee"></div>
        </div>

        <!-- Origine/Destination -->
        <div class="form-group">
            <label for="origine">Origine:</label>
            <input type="text" id="origine" name="origine" required>
            <div class="error-message" data-field="origine"></div>
        </div>

        <div class="form-group">
            <label for="destination">Destination:</label>
            <input type="text" id="destination" name="destination" required>
            <div class="error-message" data-field="destination"></div>
        </div>

        <!-- Prix par Classe -->
        <div class="form-group">
            <label>Prix par Classe:</label>
            <% for (ClasseSiege classeSiege : classesSiege) { %>
                <div class="form-group sub-form-group">
                    <label for="prix_<%= classeSiege.getId() %>"><%= classeSiege.getNom() %>:</label>
                    <input type="number" id="prix_<%= classeSiege.getId() %>" 
                           name="prixVolClasses[<%= classeSiege.getId() %>]" 
                           step="0.01"
                           data-classe="<%= classeSiege.getId() %>">
                    <div class="error-message" data-field="prixVolClasses[<%= classeSiege.getId() %>]"></div>
                </div>
            <% } %>
        </div>

        <button type="submit">Creer</button>
    </form>

    <script>
        document.getElementById('create-vol-form').addEventListener('submit', async (e) => {
            e.preventDefault();
            
            // Reset des erreurs
            document.querySelectorAll('.error-message').forEach(el => el.textContent = '');

            // Construction de l'objet
            const payload = {
                numeroVol: e.target.numeroVol.value,
                avionId: parseInt(e.target.avionId.value),
                depart: new Date(e.target.depart.value).toISOString(),
                dureeEstimee: parseFloat(e.target.dureeEstimee.value),
                origine: e.target.origine.value,
                destination: e.target.destination.value,
                prixVolClasses: Array.from(document.querySelectorAll('[name^="prixVolClasses"]'))
                    .filter(input => input.value.trim() !== '')
                    .map(input => ({
                        classeSiegeId: parseInt(input.dataset.classe),
                        prix: parseFloat(input.value)
                    }))
            };

            try {
                const response = await fetch('<%=request.getContextPath()%>/vols/create', {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        vol:payload
                        })
                });

                const data = await response.json();
                
                if (!response.ok) {
                    if (response.status === 400) {
                        data.fields.forEach(field => {
                            const selector = `[data-field="${field.fieldName}"]`;
                            const errorEl = document.querySelector(selector);
                            if (errorEl) {
                                errorEl.textContent = field.errors.join(', ');
                            }
                        });
                        throw new Error('Validation error');
                    }
                    throw new Error(data.error || 'Erreur serveur');
                }
                alert('Vol cree avec succes');
                window.location.href = '<%=request.getContextPath()%>/vols';
                
            } catch (error) {
                console.error('Erreur:', error);
                alert(error.message);
            }
        });
    </script>
</body>
</html>