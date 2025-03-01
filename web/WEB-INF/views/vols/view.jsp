<%@ page import="mg.etu2624.ticketing.model.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.util.stream.*" %>
<%@ page import="mg.etu2624.ticketing.model.view.OccupationSiege" %>
<%
    Vol vol = (Vol) request.getAttribute("vol");
    List<OccupationSiege> occupations = (List<OccupationSiege>) request.getAttribute("occupations");
    Map<String, List<List<Object[]>>> siegesParClasse = (Map<String, List<List<Object[]>>>) request.getAttribute("siegeParRangeeParClasse");
    int maxColonne = (int) request.getAttribute("maxColonne");
    boolean isLogged = session.getAttribute("user")!=null;
    boolean isOngoing = (Boolean)request.getAttribute("isOngoing"); 
%>
<!DOCTYPE html>
<html>
<head>
    <title><%= vol.getNumeroVol() %></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/assets/css/vols.css">
</head>
<body>
    <div class="alert-container">
        <!-- Message de succès -->
        <% if (request.getAttribute("success") != null) { %>
            <div class="alert alert-success">
                <span class="alert-icon">&#10004;</span>
                <span><%= request.getAttribute("success") %></span>
                <button class="alert-close" onclick="this.parentElement.remove()">X</button>
            </div>
        <% } %>
        <!-- Message d'erreur -->
        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error">
                <span class="alert-icon">&#9888;</span>
                <span><%= request.getAttribute("error") %></span>
                <button class="alert-close" onclick="this.parentElement.remove()">X</button>
            </div>
        <% } %>
    </div>
    <div class="vol-container">
        <!-- En-tête -->
        <div class="vol-header">
            <h1><%= vol.getNumeroVol() %> <%=!isOngoing?"<span class='error-message'>(Deja Decollee)</span>":""%></h1>
            <div class="vol-infos">
                <div><%= vol.getOrigine() %> &rarr; <%= vol.getDestination() %></div>
                <div>Depart: <%= vol.getDepart() %></div>
                <div>Duree: <%= vol.getDureeEstimee() %>h</div>
                <%
                    if(isLogged){
                        %>
                        <div>Limite Creation: <%=vol.getLimiteCreation()%></div>
                        <div>Limite Annuler: <%=vol.getLimiteAnnulation()%></div>
                        <%
                    }
                %>
            </div>
        </div>

        <!-- Statistiques -->
        <div class="stats">
            <% for (OccupationSiege occ : occupations) { %>
            <div class="stat-item">
                <div class="classe"><%= occ.getClasseSiege() %></div>
                <div class="progress">
                    <div class="progress-bar" style="width:<%= (occ.getNombreOccupe()*100)/occ.getNombreTotalSieges() %>%"></div>
                </div>
                <div class="prix"><%= String.format("%,.2f", occ.getPrix()) %> Ar</div>
            </div>
            <% } %>
        </div>

        <!-- Sièges par classe -->
        <% for (Map.Entry<String, List<List<Object[]>>> entry : siegesParClasse.entrySet()) { 
            String classe = entry.getKey();
        %>
        <div class="classe-section" style="--cols: <%= maxColonne %>;">
            <h2>Classe <%= classe %></h2>
            
            <% for (List<Object[]> rangee : entry.getValue()) { %>
            <div class="rangee">
                <% for (Object[] siegeData : rangee) { 
                    Siege siege = (Siege) siegeData[0];
                    boolean estReserve = (Boolean) siegeData[1];
                    Promotion promotion = (Promotion) siegeData[2];
                    boolean promoActive = promotion != null && promotion.getPromotionUtilisee() < promotion.getLimiteSieges();
                    Reservation reservation = (Reservation)siegeData[3];
                    Long reservationId = reservation != null ? reservation.getId() : null;
               %>
                <div class="siege <%= estReserve ? "reserve" : "" %> <%= promoActive ? "promotion" : "" %>"
                     data-reduction="-<%= promoActive ? promotion.getPourcentageReduction() : 0 %>%"
                     data-tooltip="<%= siege.getNumeroSiege() %><%  
                                        if(promoActive){
                                            %>&#10;Promotion : -<%=promotion.getPourcentageReduction()%>%<%
                                        }
                                        
                                        if(reservation != null){
                                            %>&#10;Reserve <%= session.getAttribute("user") != null ? "par: "+reservation.getNomPassager().replace("'", "\\'") : "" %><%
                                        }
                                    %>"
                    onclick="showSeatMenu(event, '<%= siege.getId() %>', <%= estReserve %>, <%= reservationId %>,<%=isLogged%>)">
                    <%= siege.getNumeroSiege().split("-")[2].replace("S", "") %>
                </div>
                <% } %>
            </div>
            <% } %>
        </div>
        <% } %>
        
    </div>
    <div id="seatMenu" class="seat-menu">
        <a href="<%=request.getContextPath()%>/reservations?volId=<%= vol.getId() %>&siegeId="><button>Reserver</button></a>
        <a href="<%=request.getContextPath()%>/reservations/annuler?id="><button>Annuler</button></a>
    </div>
    <script>
    let currentSeatId = null;

    function showSeatMenu(event, seatId, isReserved,reservationId,isLogged) {
        const menu = document.getElementById('seatMenu');
        
        // Masquer si déjà visible
        if (menu.style.display === 'flex') {
            menu.style.display = 'none';
            return;
        }

        const sidebar = document.getElementById('sidebar');
        // Positionner le menu
        menu.style.left = (event.pageX-sidebar.getBoundingClientRect().width) + 'px';
        menu.style.top = event.pageY + 'px';
        menu.style.display = 'flex';

        // Stocker le siège sélectionné
        currentSeatId = seatId;

         // Mise à jour des href des liens
        const createLink = menu.querySelector('a[href*="reservations?volId="]');
        const cancelLink = menu.querySelector('a[href*="reservations/annuler"]');
        
        // Remplacer l'ancien seatId dans l'URL par le nouveau
        createLink.href = createLink.href.replace(/siegeId=[^&]*/, 'siegeId=' + seatId);
        cancelLink.href = cancelLink.href.replace(/id=[^&]*/, 'id=' + reservationId);

        const buttons = menu.querySelectorAll('button');
        buttons[0].style.display = isReserved ? 'none' : 'block';
        buttons[1].style.display = isReserved && isLogged ? 'block' : 'none';
        console.log(isReserved);
    }
    </script>
</body>
</html>