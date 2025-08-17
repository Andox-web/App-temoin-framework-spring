<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="mg.etu2624.ticketing.model.Reservation" %>
<%
    Reservation reservation = (Reservation) request.getAttribute("reservation");
    boolean isLogged = session.getAttribute("user")!=null;
%>
<!DOCTYPE html>
<html>
<head>
    <title>Détails réservation</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/assets/css/view.css">
    <style>
        .view-container {
            width: 100%;
            max-width: 800px;
            background: white;
            border-radius: 16px;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
            padding: 40px;
            margin: 20px 0;
            position: relative;
            overflow: hidden;
        }

        .view-container::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            height: 5px;
            background: linear-gradient(90deg, #3498db, #2979af);
        }

        /* Titre */
        .view-container h1 {
            color: #2979af;
            margin-bottom: 30px;
            padding-bottom: 15px;
            border-bottom: 2px solid #e1f0fa;
            display: flex;
            align-items: center;
            gap: 15px;
        }

        /* Tableau de détails */
        .detail-table {
            width: 100%;
            border-collapse: collapse;
            margin: 25px 0;
            font-size: 1.05rem;
            border-radius: 10px;
            overflow: hidden;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
        }

        .detail-table th, 
        .detail-table td {
            padding: 18px 25px;
            text-align: left;
            border-bottom: 1px solid #e0e0e0;
        }

        .detail-table th {
            background-color: #e1f0fa;
            color: #2979af;
            font-weight: 600;
            width: 30%;
        }

        .detail-table tr:last-child th, 
        .detail-table tr:last-child td {
            border-bottom: none;
        }

        .detail-table tr:nth-child(even) {
            background-color: #f9fbfd;
        }

        .detail-table tr:hover {
            background-color: #e1f0fa;
        }

        /* Boutons d'action */
        .actions {
            display: flex;
            justify-content: space-between;
            flex-wrap: wrap;
            gap: 15px;
            margin-top: 30px;
            padding-top: 20px;
            border-top: 1px solid #e0e0e0;
        }

        .actions a {
            display: inline-block;
            padding: 14px 28px;
            border-radius: 50px;
            font-weight: 600;
            font-size: 16px;
            text-decoration: none;
            transition: all 0.3s ease;
            text-align: center;
            flex: 1;
            min-width: 200px;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 10px;
        }

        .actions a:first-child {
            background: linear-gradient(90deg, #3498db, #2979af);
            color: #eeeeee;
            box-shadow: 0 4px 12px rgba(52, 152, 219, 0.3);
        }

        .actions a:last-child {
            background: linear-gradient(90deg, #e74c3c, #c0392b);
            color: #eeeeee;
            box-shadow: 0 4px 12px rgba(231, 76, 60, 0.3);
        }

        .actions a:hover {
            transform: translateY(-3px);
            box-shadow: 0 6px 15px rgba(0, 0, 0, 0.2);
        }

        /* Badge pour les valeurs importantes */
        .value-badge {
            display: inline-block;
            padding: 6px 12px;
            border-radius: 20px;
            font-weight: 600;
            background-color: rgba(46, 204, 113, 0.2);
            color: #2ecc71;
            margin-top: 8px;
        }

        /* Style pour le lien passeport */
        .passport-link {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            padding: 8px 16px;
            background: #e1f0fa;
            border-radius: 6px;
            color: #2979af;
            text-decoration: none;
            font-weight: 500;
            transition: all 0.3s ease;
        }

        .passport-link:hover {
            background: #3498db;
            color: #eeeeee;
        }

        /* Responsive */
        @media (max-width: 768px) {
            .view-container {
                padding: 25px;
            }
            
            .detail-table {
                display: block;
                overflow-x: auto;
            }
            
            .actions {
                flex-direction: column;
            }
            
            .actions a {
                width: 100%;
            }
        }

        /* Animation */
        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(20px); }
            to { opacity: 1; transform: translateY(0); }
        }

        .view-container {
            animation: fadeIn 0.6s ease-out;
        }

        /* Icônes */
        .view-container h1::before {
            content: '\f072'; /* FontAwesome plane icon */
            font-family: 'Font Awesome 5 Free';
            font-weight: 900;
            font-size: 1.8rem;
            color: #3498db;
        }

        /* Améliorations pour les icônes */
        .detail-table i {
            width: 24px;
            text-align: center;
            margin-right: 8px;
            color: #3498db;
        }
    /* Icônes Font Awesome */
    @font-face {
        font-family: 'Font Awesome 5 Free';
        font-style: normal;
        font-weight: 900;
        src: url("https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/webfonts/fa-solid-900.woff2") format("woff2");
    }
    </style>

    <!-- Ajout des icônes Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
    <div class="view-container">
        <h1>Détails de la réservation #<%= reservation.getId() %></h1>

        <table class="detail-table">
            <tr>
                <th>Vol</th>
                <td><%= reservation.getVol().getNumeroVol() %></td>
            </tr>
            <tr>
                <th>Siège</th>
                <td><%= reservation.getSiege().getNumeroSiege() %> 
                    (Classe : <%= reservation.getClasseSiege().getNom() %>)
                </td>
            </tr>
            <tr>
                <th>Passager</th>
                <td><%= reservation.getNomPassager() %> (<%= reservation.getEmailPassager() %>)</td>
            </tr>
            <tr>
                <th>Date de réservation</th>
                <td><%= reservation.getDateReservation() %></td>
            </tr>
            <tr>
                <th>Prix</th>
                <td><%= reservation.getPrix() != null ? reservation.getPrix()+" Ar" : "-" %></td>
            </tr>
            <tr>
                <th>Promotion</th>
                <td>
                    <%= reservation.getPromotion() != null 
                        ? reservation.getPromotion().getDescription()+" ("+reservation.getPromotion().getPourcentageReduction()+" %)" 
                        : "-" %>
                </td>
            </tr>
            <tr>
                <th>Passeport</th>
                <td>
                    <a href="${pageContext.request.contextPath}/resources/passport/<%= reservation.getPassportPassager() %>" target="_blank">
                        Voir le passeport
                    </a>
                </td>
            </tr>
        </table>
        <a href='<%=request.getAttribute("url")%>/api/reservations/<%= reservation.getId() %>/download'>Telecharger pdf</a>
        <div class="actions">
            <a href="${pageContext.request.contextPath}/vols/view/<%= reservation.getVol().getId() %>">Retour au vol</a>
            <% if (isLogged) { %>
                <a href="${pageContext.request.contextPath}/reservations/annuler?id=<%= reservation.getId() %>" 
                onclick="return confirm('Annuler cette réservation ?')">
                Annuler la réservation
                </a>
            <% } %>
        </div>
    </div>
</body>
</html>
