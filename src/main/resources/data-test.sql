-- =====================================================
-- DONNÉES DE TEST - COURRIERS PÉLICAN
-- =====================================================

-- Nettoyer les données existantes (optionnel)
-- DELETE FROM historique_courriers;
-- DELETE FROM courriers;

-- =====================================================
-- COURRIERS ENTRANTS
-- =====================================================

INSERT INTO courriers (numero, objet, expediteur, destinataire, type, statut, priorite, contenu, archive, created_at, updated_at, createur_id)
VALUES
('CE-2026-001', 'Demande de subvention projet infrastructure', 'Ministère des Finances', 'Direction Générale', 'ENTRANT', 'NOUVEAU', 'HAUTE',
'Suite à notre réunion du mois dernier, nous vous soumettons officiellement notre demande de subvention pour le projet d''infrastructure numérique. Le montant sollicité est de 50 millions FCFA.',
false, NOW() - INTERVAL '5 days', NOW() - INTERVAL '5 days', 1),

('CE-2026-002', 'Convocation réunion inter-ministérielle', 'Cabinet du Premier Ministre', 'Directeur Général', 'ENTRANT', 'EN_COURS', 'URGENTE',
'Vous êtes convoqué à une réunion inter-ministérielle le 15 juin 2026 à 10h00 au Palais de la République. Présence obligatoire.',
false, NOW() - INTERVAL '4 days', NOW() - INTERVAL '3 days', 1),

('CE-2026-003', 'Rapport annuel audit interne 2025', 'Cabinet d''Audit KPMG', 'Service Comptabilité', 'ENTRANT', 'VALIDE', 'NORMALE',
'Veuillez trouver ci-joint le rapport complet de l''audit interne pour l''exercice 2025. Ce rapport met en évidence plusieurs points d''amélioration.',
false, NOW() - INTERVAL '10 days', NOW() - INTERVAL '8 days', 1),

('CE-2026-004', 'Plainte fournisseur - Retard de paiement', 'Société TECH AFRICA SARL', 'Direction Administrative', 'ENTRANT', 'REJETE', 'HAUTE',
'Nous attirons votre attention sur les retards de paiement de nos factures. La facture N°2025-089 d''un montant de 8.500.000 FCFA est impayée depuis 90 jours.',
false, NOW() - INTERVAL '15 days', NOW() - INTERVAL '12 days', 1),

('CE-2026-005', 'Demande de partenariat technologique', 'Orange Sénégal', 'Direction Générale', 'ENTRANT', 'EN_COURS', 'NORMALE',
'Orange Sénégal vous propose un partenariat stratégique pour la digitalisation de vos services. Nous souhaitons organiser une réunion de présentation.',
false, NOW() - INTERVAL '3 days', NOW() - INTERVAL '2 days', 1),

('CE-2026-006', 'Note de service - Congés annuels 2026', 'RH - Ministère', 'Tous les agents', 'ENTRANT', 'NOUVEAU', 'BASSE',
'La période de congés annuels 2026 est fixée du 1er juillet au 31 août. Les demandes doivent être soumises avant le 15 juin.',
false, NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day', 1),

('CE-2026-007', 'Invitation formation leadership - CESAG', 'CESAG Business School', 'DRH', 'ENTRANT', 'VALIDE', 'NORMALE',
'Le CESAG organise une formation en leadership et management pour cadres supérieurs du 20 au 24 juin 2026. Coût: 350.000 FCFA/participant.',
false, NOW() - INTERVAL '7 days', NOW() - INTERVAL '5 days', 1),

('CE-2026-008', 'Demande de visa administratif', 'Ambassade de France', 'Service Protocole', 'ENTRANT', 'ARCHIVE', 'NORMALE',
'Demande de visa administratif pour la délégation sénégalaise participant au forum économique de Paris du 10 au 12 mai 2026.',
true, NOW() - INTERVAL '30 days', NOW() - INTERVAL '25 days', 1),

-- =====================================================
-- COURRIERS SORTANTS
-- =====================================================

('CS-2026-001', 'Réponse appel d''offres - Fourniture matériel informatique', 'Direction des Achats', 'Fournisseurs agréés', 'SORTANT', 'VALIDE', 'HAUTE',
'En réponse à votre appel d''offres N°AO-2026-015, nous vous adressons notre cahier des charges technique et financier pour la fourniture de 50 ordinateurs portables.',
false, NOW() - INTERVAL '6 days', NOW() - INTERVAL '4 days', 1),

('CS-2026-002', 'Rapport mensuel activités - Mai 2026', 'Direction Générale', 'Ministère de tutelle', 'SORTANT', 'EN_COURS', 'NORMALE',
'Veuillez trouver ci-joint le rapport mensuel des activités du mois de mai 2026. Ce rapport présente les indicateurs de performance et les réalisations.',
false, NOW() - INTERVAL '2 days', NOW() - INTERVAL '1 day', 1),

('CS-2026-003', 'Lettre de félicitations - Journée nationale', 'Directeur Général', 'Partenaires institutionnels', 'SORTANT', 'VALIDE', 'BASSE',
'À l''occasion de la Journée Nationale de notre institution, nous vous adressons nos sincères félicitations et vous renouvelons notre engagement de collaboration.',
false, NOW() - INTERVAL '8 days', NOW() - INTERVAL '7 days', 1),

('CS-2026-004', 'Transmission budget prévisionnel 2027', 'DAF', 'Ministère des Finances', 'SORTANT', 'NOUVEAU', 'URGENTE',
'Conformément aux directives, nous vous transmettons le budget prévisionnel 2027 pour examen et approbation. Le montant total est de 2,8 milliards FCFA.',
false, NOW() - INTERVAL '1 day', NOW(), 1),

('CS-2026-005', 'Demande de formation - Personnel technique', 'DRH', 'Institut de Formation', 'SORTANT', 'VALIDE', 'NORMALE',
'Dans le cadre du plan de formation 2026, nous sollicitons votre offre de formation pour 15 agents techniques dans les domaines: cybersécurité, développement web.',
false, NOW() - INTERVAL '12 days', NOW() - INTERVAL '10 days', 1),

('CS-2026-006', 'Procès-verbal réunion de direction', 'Secrétariat Général', 'Membres du Comité', 'SORTANT', 'VALIDE', 'NORMALE',
'Suite à la réunion de direction du 28 mai 2026, veuillez trouver ci-joint le procès-verbal pour signature et approbation.',
false, NOW() - INTERVAL '5 days', NOW() - INTERVAL '3 days', 1);

-- =====================================================
-- HISTORIQUES
-- =====================================================

INSERT INTO historique_courriers (courrier_id, user_id, action, commentaire, date)
VALUES
(2, 1, 'AFFECTE', 'Affecté au chef de service pour traitement urgent', NOW() - INTERVAL '3 days'),
(3, 1, 'VALIDE', 'Rapport vérifié et approuvé par la direction', NOW() - INTERVAL '8 days'),
(4, 1, 'REJETE', 'Dossier incomplet - Pièces justificatives manquantes', NOW() - INTERVAL '12 days'),
(7, 1, 'VALIDE', 'Formation approuvée dans le cadre du plan annuel', NOW() - INTERVAL '5 days'),
(8, 1, 'VALIDE', 'Visa accordé - Dossier archivé', NOW() - INTERVAL '25 days'),
(9, 1, 'VALIDE', 'Offre conforme aux exigences techniques', NOW() - INTERVAL '4 days');

