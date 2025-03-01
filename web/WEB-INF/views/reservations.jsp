<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="mg.etu2624.ticketing.model.view.PrixSiegeView" %>
<%@ page import="mg.itu.prom16.validation.BindingResult" %>
<%@ page import="mg.itu.prom16.validation.Field" %>
<%@ page import="mg.etu2624.ticketing.model.*" %>
<%
    PrixSiegeView prix = (PrixSiegeView) request.getAttribute("prix");
    BindingResult result = (BindingResult) request.getAttribute("result");
    boolean haserror = result != null && result.hasErrors();
    Vol vol = (Vol) request.getAttribute("vol");
    Siege siege = (Siege) request.getAttribute("siege");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Réservation - Siège ${prix.numeroSiege}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/assets/css/form.css">
    <script>
        function actualiserPrix() {
            fetch('${pageContext.request.contextPath}/reservations/prix?volId=${vol.id}&siegeId=${siege.id}')
                .then(response => response.json())
                .then(data => {
                    document.getElementById('prixBase').value = data.prixBase + ' Ar' + (data.promotion ? ' (- ' + data.promotion.pourcentageReduction + ' %)' : '');
                    document.getElementById('prixFinal').value = data.prixFinal + ' Ar';
                    document.getElementById('promotionId').value = data.promotion ? data.promotion.id : '';
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
</html>