<!-- <%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="mg.etu2624.ticketing.model.view.PrixSiegeView" %>
<%@ page import="mg.etu2624.ticketing.model.Categorie" %>
<%@ page import="mg.itu.prom16.validation.BindingResult" %>
<%@ page import="mg.itu.prom16.validation.Field" %>
<%@ page import="mg.etu2624.ticketing.model.*" %>
<%@ page import="java.util.*" %>
<%
    PrixSiegeView prix = (PrixSiegeView) request.getAttribute("prix");
    BindingResult result = (BindingResult) request.getAttribute("result");
    boolean haserror = result != null && result.hasErrors();
    Vol vol = (Vol) request.getAttribute("vol");
    Siege siege = (Siege) request.getAttribute("siege");
    List<Categorie> categories = (List<Categorie>) request.getAttribute("categories");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Réservation - Siège ${prix.numeroSiege}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/assets/css/form.css">
    <script>
        function actualiserPrix() {
            const catId = document.getElementById('categorieId').value;
            fetch('${pageContext.request.contextPath}/reservations/prix?volId=${vol.id}&siegeId=${siege.id}&categorieId=' + catId)
                .then(response => response.json())
                .then(data => {
                    document.getElementById('prixBase').value = data.prixBase + ' Ar' + (data.promotion ? ' (- ' + data.promotion.pourcentageReduction + ' %)' : '');
                    document.getElementById('prixFinal').value = data.prixFinal + ' Ar';
                    document.getElementById('promotionId').value = data.promotion ? data.promotion.id : '';
                    document.getElementById('categorieNom').value = data.categorieNom || '';
                })
                .catch(error => console.error('Erreur:', error));
        }

        actualiserPrix();
        setInterval(actualiserPrix, 30000);
    </script>
</head>
<body>
    <div class="form-container">
        <h1>Réservation du siège ${prix.numeroSiege}</h1>
        <c:if test="${not empty error}">
            <div class="error-message">${error}</div>
        </c:if>
        <form method="post" enctype="multipart/form-data">
            <input type="hidden" name="volId" value="${vol.id}">
            <input type="hidden" name="siegeId" value="${siege.id}">
            <input type="hidden" name="promotionId" id="promotionId">
            <div class="form-group">
                <label>Vol :</label>
                <input type="text" value="${vol.numeroVol}" readonly>
            </div>

            <div class="form-group">
                <label>Classe :</label>
                <input type="text" value="${siege.classeSiege.nom}" readonly>
            </div>
            <div class="form-group">
                <label>Siege :</label>
                <input type="text" value="${siege.numeroSiege}" readonly>
            </div>
            <div class="form-group">
                <label>Catégorie :</label>
                <select name="categorieId" id="categorieId" onchange="actualiserPrix()">
                    <% if (categories != null) {
                        for (Categorie cat : categories) { %>
                            <option value="<%= cat.getId() %>" <%= (prix.getCategorieId() != null && prix.getCategorieId().equals(cat.getId())) ? "selected" : "" %>><%= cat.getNom() %></option>
                    <%  }
                    } %>
                </select>
            </div>
            <input type="hidden" name="categorieNom" id="categorieNom" value="<%= prix.getCategorieNom() %>">
            <div class="form-group">
                <label>Prix de base :</label>
                <input type="text" id="prixBase" readonly>
            </div>

            <div class="form-group">
                <label>Prix final :</label>
                <input type="text" id="prixFinal" readonly>
            </div>

            <div class="form-group">
                <label>Nom complet :</label>
                <input type="text" name="nomPassager" value='${result.getErrors("nomPassager") != null ? result.getErrors("nomPassager").fieldValue : ""}' >
                <c:if test="${haserror}">
                    <span class="error-message">${result.getErrors('nomPassager').firstError}</span>
                </c:if>
            </div>

            <div class="form-group">
                <label>Email :</label>
                <input type="email" name="emailPassager" value='${result.getErrors("emailPassager") != null ? result.getErrors("emailPassager").fieldValue : ""}' >
                <c:if test="${haserror}">
                    <span class="error-message">${result.getErrors('emailPassager').firstError}</span>
                </c:if>
            </div>

            <div class="form-group">
                <label>Copie passeport (PDF max 200MB) :</label>
                <input type="file" name="passportPassager" accept="application/pdf">
                <c:if test="${haserror}">
                    <span class="error-message">${result.getErrors('passportPassager').firstError}</span>
                </c:if>
            </div>

            <button type="submit">Confirmer la réservation</button>
        </form>
    </div>
</body>
</html> -->

<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Réservation</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/assets/css/form.css">
    <style>
        .error-message {
            color: #d32f2f;
            font-size: 0.85rem;
            margin-top: 0.25rem;
            display: block;
        }
        .success-message {
            color: #2e7d32;
            background-color: #e8f5e9;
            padding: 10px;
            border-radius: 4px;
            margin-bottom: 15px;
        }
        .form-container {
            max-width: 800px;
            margin: 30px auto;
            padding: 25px;
            background: #fff;
            border-radius: 8px;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
        }
        .form-group {
            margin-bottom: 20px;
        }
        label {
            display: block;
            margin-bottom: 8px;
            font-weight: 600;
            color: #333;
        }
        input[type="text"], input[type="email"], select {
            width: 100%;
            padding: 10px 12px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 16px;
            transition: border-color 0.3s;
        }
        input[type="text"]:focus, input[type="email"]:focus, select:focus {
            border-color: #1976d2;
            outline: none;
            box-shadow: 0 0 0 2px rgba(25, 118, 210, 0.2);
        }
        input[readonly] {
            background-color: #f5f5f5;
            cursor: not-allowed;
        }
        button {
            background-color: #1976d2;
            color: white;
            border: none;
            padding: 12px 24px;
            font-size: 16px;
            border-radius: 4px;
            cursor: pointer;
            transition: background-color 0.3s;
            display: block;
            width: 100%;
            margin-top: 20px;
        }
        button:hover {
            background-color: #1565c0;
        }
        .price-display {
            font-weight: bold;
            font-size: 18px;
        }
    </style>
    <script>
        // Variables globales
        let volId = new URLSearchParams(window.location.search).get('volId');
        let siegeId = new URLSearchParams(window.location.search).get('siegeId');
        
        // Chargement initial des données
        document.addEventListener('DOMContentLoaded', function() {
            if (!volId || !siegeId) {
                alert('Paramètres volId et siegeId requis');
                return;
            }
            
            // Vérifier si nous avons un message de succès
            const urlParams = new URLSearchParams(window.location.search);
            if (urlParams.has('reservationSuccess')) {
                showSuccessMessage('Réservation effectuée avec succès!');
            }
            
            // Charger les données du siège et du vol
            fetch('<%=request.getAttribute("url")%>/api/reservations/init?volId='+volId+'&siegeId='+siegeId)
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Erreur réseau');
                    }
                    return response.json();
                })
                .then(data => {
                    document.getElementById('volNumero').value = data.vol.numeroVol;
                    document.getElementById('classeSiege').value = data.siege.classeSiege.nom;
                    document.getElementById('numeroSiege').value = data.siege.numeroSiege;
                    
                    // Remplir les catégories
                    const select = document.getElementById('categorieId');
                    data.categories.forEach(cat => {
                        const option = document.createElement('option');
                        option.value = cat.id;
                        option.textContent = cat.nom;
                        select.appendChild(option);
                    });
                    
                    // Charger le prix initial
                    actualiserPrix();
                })
                .catch(error => {
                    console.error('Erreur:', error);
                    showErrorMessage('Erreur lors du chargement des données: ' + error.message);
                });
        });
        
        // Actualiser le prix en fonction de la catégorie sélectionnée
        function actualiserPrix() {
            const categorieId = document.getElementById('categorieId').value;
            const prixBaseElement = document.getElementById('prixBase');
            const prixFinalElement = document.getElementById('prixFinal');
            prixBaseElement.innerHTML = '';
            prixFinalElement.innerHTML = '';

            // Réinitialiser l'erreur de catégorie
            document.getElementById('error-categorieId').style.display = 'none';

            if (!categorieId) {
                prixBaseElement.innerHTML = '';
                prixFinalElement.innerHTML = '';
                return;
            }

            fetch('<%=request.getAttribute("url")%>/api/reservations/prix?volId='+volId+'&siegeId='+siegeId+'&categorieId='+categorieId)
            .then(response => {
                if (!response.ok) {
                throw new Error('Erreur lors du calcul du prix');
                }
                return response.json();
            })
            .then(data => {
                if (data.error) {
                    prixBaseElement.innerHTML = '';
                    prixFinalElement.innerHTML = '';
                    showErrorMessage(data.error);
                return;
                }
                if (!data.prixBase || !data.prixFinal) {
                prixBaseElement.innerHTML = '';
                prixFinalElement.innerHTML = '';
                showErrorMessage('Prix non disponible pour cette catégorie.');
                return;
                }
                prixBaseElement.innerHTML = data.prixBase.toLocaleString('fr') + ' Ar' +
                (data.promotion ? ' <span style="color: #d32f2f; font-size: 0.9em;">(-' + data.promotion.pourcentageReduction + '%)</span>' : '');
                prixFinalElement.innerHTML = '<strong>' + data.prixFinal.toLocaleString('fr') + ' Ar</strong>';
                document.getElementById('promotionId').value = data.promotion ? data.promotion.id : '';
                document.getElementById('categorieNom').value = data.categorieNom || '';
            })
            .catch(error => {
                prixBaseElement.innerHTML = '';
                prixFinalElement.innerHTML = '';
                showErrorMessage('Erreur lors du calcul du prix: ' + error.message);
            });
        }
        
        // Soumission du formulaire avec Fetch API
        function submitReservation(event) {
            event.preventDefault();
            
            // Afficher un indicateur de chargement
            const submitButton = document.querySelector('button[type="submit"]');
            submitButton.disabled = true;
            submitButton.innerHTML = 'Traitement en cours...';
            
            // Créer FormData à partir du formulaire
            const formElement = document.getElementById('reservationForm');
            const formData = new FormData(formElement);
            
            // Envoyer la requête à l'API
            fetch('<%=request.getAttribute("url")%>/api/reservations', {
                method: 'POST',
                body: formData
            })
            .then(response => {
                submitButton.disabled = false;
                submitButton.innerHTML = 'Confirmer la réservation';
                
                if (response.ok) {
                    return response.json();
                } else {
                    return response.json().then(err => Promise.reject(err));
                }
            })
            .then(data => {
                if (data.success) {
                    // Rediriger vers la page de confirmation
                    window.location.href = '/Ticketing/vols/view/'+volId;
                } else {
                    displayErrors(data|| {});
                }
            })
            .catch(error => {
                submitButton.disabled = false;
                submitButton.innerHTML = 'Confirmer la réservation';
                
                console.error('Erreur:', error);
                
                if (error.error) {
                    displayErrors(error);
                } else {
                    showErrorMessage('Une erreur est survenue: ' + error.message);
                }
            });
        }
        
        // Afficher les erreurs de validation
        function displayErrors(errors) {
            // Réinitialiser les erreurs précédentes
            
            document.querySelectorAll('.error-message').forEach(el => {
                el.textContent = '';
                el.style.display = 'none';
            });
            console.log(errors);
            
            if(errors.error){
                showErrorMessage(errors.error);
                return;
            }

            // Afficher les nouvelles erreurs
            for (const [field, message] of Object.entries(errors)) {
                const errorElement = document.getElementById('error-'+field);
                if (errorElement) {
                    errorElement.textContent = message;
                    errorElement.style.display = 'block';
                } else {
                    // Pour les erreurs générales
                    showErrorMessage(message);
                }
            }
        }
        
        // Afficher un message d'erreur
        function showErrorMessage(message) {
            const errorContainer = document.getElementById('error-general');
            errorContainer.innerHTML = message;
            errorContainer.style.display = 'block';
            
            // Masquer après 5 secondes
            setTimeout(() => {
                errorContainer.style.display = 'none';
            }, 60000);
        }
        
        // Afficher un message de succès
        function showSuccessMessage(message) {
            const successContainer = document.getElementById('success-message');
            successContainer.innerHTML = message;
            successContainer.style.display = 'block';
            
            // Masquer après 5 secondes
            setTimeout(() => {
                successContainer.style.display = 'none';
            }, 60000);
        }
    </script>
</head>
<body>
    <div class="form-container">
        <h1>Réservation de siège</h1>
        
        <div id="success-message" class="success-message" style="display:none;"></div>
        <div id="error-general" class="error-message" style="display:none;"></div>
        
        <form id="reservationForm" onsubmit="submitReservation(event)" enctype="multipart/form-data">
            <input type="hidden" name="volId" value="${param.volId}">
            <input type="hidden" name="siegeId" value="${param.siegeId}">
            <input type="hidden" name="promotionId" id="promotionId">
            <input type="hidden" name="categorieNom" id="categorieNom">
            
            <div class="form-group">
                <label>Vol :</label>
                <input type="text" id="volNumero" readonly>
            </div>

            <div class="form-group">
                <label>Classe :</label>
                <input type="text" id="classeSiege" readonly>
            </div>
            
            <div class="form-group">
                <label>Siège :</label>
                <input type="text" id="numeroSiege" readonly>
            </div>
            
            <div class="form-group">
                <label>Catégorie :</label>
                <select name="categorieId" id="categorieId" onchange="actualiserPrix()">
                    <option value="">-- Sélectionnez une catégorie --</option>
                    <!-- Options chargées dynamiquement -->
                </select>
                <span id="error-categorieId" class="error-message" style="display:none;"></span>
            </div>
            
            <div class="form-group">
                <label>Prix de base :</label>
                <div id="prixBase" class="price-display">Chargement...</div>
            </div>

            <div class="form-group">
                <label>Prix final :</label>
                <div id="prixFinal" class="price-display">Chargement...</div>
            </div>

            <div class="form-group">
                <label>Nom complet :</label>
                <input type="text" name="nomPassager" id="nomPassager" placeholder="Nom et prénom">
                <span id="error-nomPassager" class="error-message" style="display:none;"></span>
            </div>

            <div class="form-group">
                <label>Email :</label>
                <input type="email" name="emailPassager" id="emailPassager" placeholder="email@example.com">
                <span id="error-emailPassager" class="error-message" style="display:none;"></span>
            </div>

            <div class="form-group">
                <label>Copie passeport (PDF max 200MB) :</label>
                <input type="file" name="passportPassager" id="passportPassager" accept="application/pdf">
                <span id="error-passportPassager" class="error-message" style="display:none;"></span>
            </div>

            <button type="submit">Confirmer la réservation</button>
        </form>
    </div>
</body>
</html>