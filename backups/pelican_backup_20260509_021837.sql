--
-- PostgreSQL database dump
--

\restrict UHZoIfxe8mfb3JqW4BBPJ17ayq5TPShi0kykaaI1s3YQfAuHAPaLQAT8xSMQKR2

-- Dumped from database version 15.17 (Homebrew)
-- Dumped by pg_dump version 15.17 (Homebrew)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: public; Type: SCHEMA; Schema: -; Owner: pelican_user
--

-- *not* creating schema, since initdb creates it


ALTER SCHEMA public OWNER TO pelican_user;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: audit_logs; Type: TABLE; Schema: public; Owner: pelican_user
--

CREATE TABLE public.audit_logs (
    id bigint NOT NULL,
    action character varying(255),
    created_at timestamp(6) without time zone,
    details text,
    ip_address character varying(255),
    resource character varying(255),
    success boolean NOT NULL,
    user_agent character varying(255),
    user_email character varying(255)
);


ALTER TABLE public.audit_logs OWNER TO pelican_user;

--
-- Name: audit_logs_id_seq; Type: SEQUENCE; Schema: public; Owner: pelican_user
--

CREATE SEQUENCE public.audit_logs_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.audit_logs_id_seq OWNER TO pelican_user;

--
-- Name: audit_logs_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: pelican_user
--

ALTER SEQUENCE public.audit_logs_id_seq OWNED BY public.audit_logs.id;


--
-- Name: courriers; Type: TABLE; Schema: public; Owner: pelican_user
--

CREATE TABLE public.courriers (
    id bigint NOT NULL,
    archive boolean NOT NULL,
    contenu text,
    created_at timestamp(6) without time zone,
    destinataire character varying(255),
    expediteur character varying(255),
    fichier_nom character varying(255),
    fichier_path character varying(255),
    numero character varying(255) NOT NULL,
    objet character varying(255) NOT NULL,
    priorite character varying(255),
    statut character varying(255) NOT NULL,
    type character varying(255) NOT NULL,
    updated_at timestamp(6) without time zone,
    assigne_a_id bigint,
    createur_id bigint NOT NULL,
    CONSTRAINT courriers_priorite_check CHECK (((priorite)::text = ANY ((ARRAY['BASSE'::character varying, 'NORMALE'::character varying, 'HAUTE'::character varying, 'URGENTE'::character varying])::text[]))),
    CONSTRAINT courriers_statut_check CHECK (((statut)::text = ANY ((ARRAY['NOUVEAU'::character varying, 'EN_COURS'::character varying, 'VALIDE'::character varying, 'REJETE'::character varying, 'ARCHIVE'::character varying])::text[]))),
    CONSTRAINT courriers_type_check CHECK (((type)::text = ANY ((ARRAY['ENTRANT'::character varying, 'SORTANT'::character varying])::text[])))
);


ALTER TABLE public.courriers OWNER TO pelican_user;

--
-- Name: courriers_id_seq; Type: SEQUENCE; Schema: public; Owner: pelican_user
--

CREATE SEQUENCE public.courriers_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.courriers_id_seq OWNER TO pelican_user;

--
-- Name: courriers_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: pelican_user
--

ALTER SEQUENCE public.courriers_id_seq OWNED BY public.courriers.id;


--
-- Name: historique_courriers; Type: TABLE; Schema: public; Owner: pelican_user
--

CREATE TABLE public.historique_courriers (
    id bigint NOT NULL,
    action character varying(255) NOT NULL,
    commentaire text,
    date timestamp(6) without time zone,
    courrier_id bigint NOT NULL,
    user_id bigint,
    CONSTRAINT historique_courriers_action_check CHECK (((action)::text = ANY ((ARRAY['CREATION'::character varying, 'AFFECTATION'::character varying, 'TRANSFERT'::character varying, 'VALIDATION'::character varying, 'REJET'::character varying, 'ARCHIVAGE'::character varying, 'MODIFICATION'::character varying])::text[])))
);


ALTER TABLE public.historique_courriers OWNER TO pelican_user;

--
-- Name: historique_courriers_id_seq; Type: SEQUENCE; Schema: public; Owner: pelican_user
--

CREATE SEQUENCE public.historique_courriers_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.historique_courriers_id_seq OWNER TO pelican_user;

--
-- Name: historique_courriers_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: pelican_user
--

ALTER SEQUENCE public.historique_courriers_id_seq OWNED BY public.historique_courriers.id;


--
-- Name: notifications; Type: TABLE; Schema: public; Owner: pelican_user
--

CREATE TABLE public.notifications (
    id bigint NOT NULL,
    created_at timestamp(6) without time zone,
    is_read boolean NOT NULL,
    lien character varying(255),
    message text,
    read_at timestamp(6) without time zone,
    titre character varying(255) NOT NULL,
    type character varying(255),
    user_id bigint NOT NULL,
    CONSTRAINT notifications_type_check CHECK (((type)::text = ANY ((ARRAY['COURRIER_ASSIGNE'::character varying, 'COURRIER_VALIDE'::character varying, 'COURRIER_REJETE'::character varying, 'WORKFLOW'::character varying, 'SYSTEME'::character varying])::text[])))
);


ALTER TABLE public.notifications OWNER TO pelican_user;

--
-- Name: notifications_id_seq; Type: SEQUENCE; Schema: public; Owner: pelican_user
--

CREATE SEQUENCE public.notifications_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.notifications_id_seq OWNER TO pelican_user;

--
-- Name: notifications_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: pelican_user
--

ALTER SEQUENCE public.notifications_id_seq OWNED BY public.notifications.id;


--
-- Name: users; Type: TABLE; Schema: public; Owner: pelican_user
--

CREATE TABLE public.users (
    id bigint NOT NULL,
    account_non_locked boolean NOT NULL,
    created_at timestamp(6) without time zone,
    email character varying(255) NOT NULL,
    enabled boolean NOT NULL,
    failed_login_attempts integer NOT NULL,
    last_login timestamp(6) without time zone,
    lock_time timestamp(6) without time zone,
    nom character varying(255) NOT NULL,
    password character varying(255) NOT NULL,
    prenom character varying(255) NOT NULL,
    role character varying(255) NOT NULL,
    service character varying(255),
    updated_at timestamp(6) without time zone,
    CONSTRAINT users_role_check CHECK (((role)::text = ANY ((ARRAY['ADMIN'::character varying, 'AGENT'::character varying, 'CHEF_SERVICE'::character varying, 'DIRECTEUR'::character varying])::text[])))
);


ALTER TABLE public.users OWNER TO pelican_user;

--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: pelican_user
--

CREATE SEQUENCE public.users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.users_id_seq OWNER TO pelican_user;

--
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: pelican_user
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- Name: audit_logs id; Type: DEFAULT; Schema: public; Owner: pelican_user
--

ALTER TABLE ONLY public.audit_logs ALTER COLUMN id SET DEFAULT nextval('public.audit_logs_id_seq'::regclass);


--
-- Name: courriers id; Type: DEFAULT; Schema: public; Owner: pelican_user
--

ALTER TABLE ONLY public.courriers ALTER COLUMN id SET DEFAULT nextval('public.courriers_id_seq'::regclass);


--
-- Name: historique_courriers id; Type: DEFAULT; Schema: public; Owner: pelican_user
--

ALTER TABLE ONLY public.historique_courriers ALTER COLUMN id SET DEFAULT nextval('public.historique_courriers_id_seq'::regclass);


--
-- Name: notifications id; Type: DEFAULT; Schema: public; Owner: pelican_user
--

ALTER TABLE ONLY public.notifications ALTER COLUMN id SET DEFAULT nextval('public.notifications_id_seq'::regclass);


--
-- Name: users id; Type: DEFAULT; Schema: public; Owner: pelican_user
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- Data for Name: audit_logs; Type: TABLE DATA; Schema: public; Owner: pelican_user
--

COPY public.audit_logs (id, action, created_at, details, ip_address, resource, success, user_agent, user_email) FROM stdin;
\.


--
-- Data for Name: courriers; Type: TABLE DATA; Schema: public; Owner: pelican_user
--

COPY public.courriers (id, archive, contenu, created_at, destinataire, expediteur, fichier_nom, fichier_path, numero, objet, priorite, statut, type, updated_at, assigne_a_id, createur_id) FROM stdin;
2	f		2026-05-05 20:03:52.42607	Direction Générale	Ministère de la Santé	Capture d’écran 2026-05-02 à 12.26.45.png	c9402d2e-9b55-42dc-ab83-1b119c682c6f.png	CE-20260505-6CA26C	demande	NORMALE	NOUVEAU	ENTRANT	2026-05-05 20:03:52.42607	\N	3
32	f	Demande urgente	2026-05-09 01:21:37.006225	Direction	DRH	\N	\N	CE-20260509-1B41C6	Demande de conge	HAUTE	NOUVEAU	ENTRANT	2026-05-09 01:21:37.006225	\N	3
33	f	Demande urgente	2026-05-09 01:23:05.75927	Direction	DRH	\N	\N	CE-20260509-9A347E	Demande de congé	HAUTE	NOUVEAU	ENTRANT	2026-05-09 01:23:05.75927	\N	3
1	t	Veuillez organiser une réunion	2026-05-04 17:10:42.026992	Direction Générale	Ministère	\N	\N	CE-20260504-11208B	Demande de réunion	HAUTE	ARCHIVE	ENTRANT	2026-05-09 01:23:54.903536	3	3
31	f	Demande urgente	2026-05-09 01:21:22.958407	Direction	DRH	\N	\N	CE-20260509-15DB64	Demande de conge	HAUTE	EN_COURS	ENTRANT	2026-05-09 01:26:16.877002	4	3
\.


--
-- Data for Name: historique_courriers; Type: TABLE DATA; Schema: public; Owner: pelican_user
--

COPY public.historique_courriers (id, action, commentaire, date, courrier_id, user_id) FROM stdin;
1	CREATION	Courrier créé	2026-05-04 17:10:42.054333	1	3
2	AFFECTATION	Affecté à Jean Dupont	2026-05-04 17:12:50.539972	1	3
3	VALIDATION	OK	2026-05-04 17:20:00.713876	1	3
4	TRANSFERT	Transfere de Jean Dupont vers Jean Dupont - Pour traitement	2026-05-04 17:42:53.540189	1	3
5	VALIDATION		2026-05-05 19:57:53.105236	1	3
6	VALIDATION		2026-05-05 19:57:59.126268	1	3
7	CREATION	Courrier cree	2026-05-05 20:03:52.444027	2	3
8	REJET		2026-05-05 20:05:07.883118	1	3
9	REJET		2026-05-05 20:05:14.187904	1	3
10	AFFECTATION	Affecte a Jean Dupont	2026-05-06 10:24:44.838277	1	3
11	VALIDATION		2026-05-06 10:24:54.793036	1	3
14	VALIDATION	Valide	2026-05-07 09:11:34.975572	1	3
15	VALIDATION	Validation test historique	2026-05-07 09:16:02.561978	1	3
17	VALIDATION	Test historique API	2026-05-07 08:26:18.103883	1	3
18	CREATION	Courrier cree	2026-05-09 01:21:22.993367	31	3
19	CREATION	Courrier cree	2026-05-09 01:21:37.009716	32	3
20	CREATION	Courrier cree	2026-05-09 01:23:05.763074	33	3
21	TRANSFERT	Transfere de Jean Dupont vers Super Admin - Urgent	2026-05-09 01:23:42.077429	1	3
22	ARCHIVAGE	Archive	2026-05-09 01:23:54.89755	1	3
23	VALIDATION	Approuve	2026-05-09 01:26:05.937887	31	3
24	REJET	Dossier incomplet	2026-05-09 01:26:10.986391	31	3
25	AFFECTATION	Affecte a Jean Dupont	2026-05-09 01:26:16.87176	31	3
\.


--
-- Data for Name: notifications; Type: TABLE DATA; Schema: public; Owner: pelican_user
--

COPY public.notifications (id, created_at, is_read, lien, message, read_at, titre, type, user_id) FROM stdin;
3	2026-05-04 17:42:53.565938	f	/courriers/1	Un courrier vous a été assigné: Demande de réunion	\N	Nouveau courrier assigné	COURRIER_ASSIGNE	4
4	2026-05-05 19:57:53.133545	f	/courriers/1	Votre courrier a été validé: Demande de réunion	\N	Courrier validé	COURRIER_VALIDE	3
5	2026-05-05 19:57:59.129519	f	/courriers/1	Votre courrier a été validé: Demande de réunion	\N	Courrier validé	COURRIER_VALIDE	3
6	2026-05-05 20:05:07.901483	f	/courriers/1	Votre courrier a été rejeté: 	\N	Courrier rejeté	COURRIER_REJETE	3
7	2026-05-05 20:05:14.190836	f	/courriers/1	Votre courrier a été rejeté: 	\N	Courrier rejeté	COURRIER_REJETE	3
8	2026-05-06 10:24:44.869071	f	/courriers/1	Un courrier vous a été assigné: Demande de réunion	\N	Nouveau courrier assigné	COURRIER_ASSIGNE	4
9	2026-05-06 10:24:54.797117	f	/courriers/1	Votre courrier a été validé: Demande de réunion	\N	Courrier validé	COURRIER_VALIDE	3
10	2026-05-07 09:11:35.022184	f	/courriers/1	Votre courrier a été validé: Demande de réunion	\N	Courrier validé	COURRIER_VALIDE	3
11	2026-05-07 09:16:02.598627	f	/courriers/1	Votre courrier a été validé: Demande de réunion	\N	Courrier validé	COURRIER_VALIDE	3
1	2026-05-04 17:12:50.603887	t	/courriers/1	Un courrier vous a été assigné: Demande de réunion	2026-05-09 01:22:49.392363	Nouveau courrier assigné	COURRIER_ASSIGNE	4
12	2026-05-09 01:23:42.081809	f	/courriers/1	Un courrier vous a été assigné: Demande de réunion	\N	Nouveau courrier assigné	COURRIER_ASSIGNE	3
13	2026-05-09 01:26:05.993691	f	/courriers/31	Votre courrier a été validé: Demande de conge	\N	Courrier validé	COURRIER_VALIDE	3
14	2026-05-09 01:26:10.989144	f	/courriers/31	Votre courrier a été rejeté: Dossier incomplet	\N	Courrier rejeté	COURRIER_REJETE	3
15	2026-05-09 01:26:16.874269	f	/courriers/31	Un courrier vous a été assigné: Demande de conge	\N	Nouveau courrier assigné	COURRIER_ASSIGNE	4
2	2026-05-04 17:20:00.73789	t	/courriers/1	Votre courrier a été validé: Demande de réunion	2026-05-09 01:26:42.551748	Courrier validé	COURRIER_VALIDE	3
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: pelican_user
--

COPY public.users (id, account_non_locked, created_at, email, enabled, failed_login_attempts, last_login, lock_time, nom, password, prenom, role, service, updated_at) FROM stdin;
5	t	2026-05-05 19:56:38.171182	noemane@gmail.com	t	0	\N	\N	Bahij	$2a$12$M3kkDeYCT.DpjvTJjSOI.upg.OecJihMQlsQZwEOIRv9irlk8afZO	Nouâmane	AGENT		2026-05-05 19:56:38.171182
3	t	2026-05-04 17:09:33.472626	admin@pelican.sn	t	0	2026-05-09 01:21:31.90873	\N	Super	$2a$12$lFVJxwux8ahdm8rOxrf21.FiDY/8HRIV.v94wQBCTD.EOmQb0nQFa	Admin	ADMIN	Direction	2026-05-09 01:21:31.913172
4	t	2026-05-04 17:09:33.90928	agent@pelican.sn	t	0	\N	\N	Dupont	$2a$12$lZqOzzg5X/lVcGFudGfmueLaMCiMgM932LCzUgFoPj.erNAAGjaB2	Jean	AGENT	Direction	2026-05-09 01:26:23.19061
6	t	2026-05-09 01:21:13.6142	moussa@pelican.sn	f	0	\N	\N	Diop	$2a$12$LeTj1jAyZ80bM8/sohttoe.8fQcmrooT193vXXdhGjG8c5hsedW6i	Moussa	AGENT	Secretariat	2026-05-09 01:26:27.885027
\.


--
-- Name: audit_logs_id_seq; Type: SEQUENCE SET; Schema: public; Owner: pelican_user
--

SELECT pg_catalog.setval('public.audit_logs_id_seq', 1, false);


--
-- Name: courriers_id_seq; Type: SEQUENCE SET; Schema: public; Owner: pelican_user
--

SELECT pg_catalog.setval('public.courriers_id_seq', 33, true);


--
-- Name: historique_courriers_id_seq; Type: SEQUENCE SET; Schema: public; Owner: pelican_user
--

SELECT pg_catalog.setval('public.historique_courriers_id_seq', 25, true);


--
-- Name: notifications_id_seq; Type: SEQUENCE SET; Schema: public; Owner: pelican_user
--

SELECT pg_catalog.setval('public.notifications_id_seq', 15, true);


--
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: pelican_user
--

SELECT pg_catalog.setval('public.users_id_seq', 6, true);


--
-- Name: audit_logs audit_logs_pkey; Type: CONSTRAINT; Schema: public; Owner: pelican_user
--

ALTER TABLE ONLY public.audit_logs
    ADD CONSTRAINT audit_logs_pkey PRIMARY KEY (id);


--
-- Name: courriers courriers_pkey; Type: CONSTRAINT; Schema: public; Owner: pelican_user
--

ALTER TABLE ONLY public.courriers
    ADD CONSTRAINT courriers_pkey PRIMARY KEY (id);


--
-- Name: historique_courriers historique_courriers_pkey; Type: CONSTRAINT; Schema: public; Owner: pelican_user
--

ALTER TABLE ONLY public.historique_courriers
    ADD CONSTRAINT historique_courriers_pkey PRIMARY KEY (id);


--
-- Name: courriers idx_courrier_numero; Type: CONSTRAINT; Schema: public; Owner: pelican_user
--

ALTER TABLE ONLY public.courriers
    ADD CONSTRAINT idx_courrier_numero UNIQUE (numero);


--
-- Name: users idx_user_email; Type: CONSTRAINT; Schema: public; Owner: pelican_user
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT idx_user_email UNIQUE (email);


--
-- Name: notifications notifications_pkey; Type: CONSTRAINT; Schema: public; Owner: pelican_user
--

ALTER TABLE ONLY public.notifications
    ADD CONSTRAINT notifications_pkey PRIMARY KEY (id);


--
-- Name: users uk_6dotkott2kjsp8vw4d0m25fb7; Type: CONSTRAINT; Schema: public; Owner: pelican_user
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT uk_6dotkott2kjsp8vw4d0m25fb7 UNIQUE (email);


--
-- Name: courriers uk_71d1cjfw2sh88mj1j3kj3ialx; Type: CONSTRAINT; Schema: public; Owner: pelican_user
--

ALTER TABLE ONLY public.courriers
    ADD CONSTRAINT uk_71d1cjfw2sh88mj1j3kj3ialx UNIQUE (numero);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: pelican_user
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: idx_audit_action; Type: INDEX; Schema: public; Owner: pelican_user
--

CREATE INDEX idx_audit_action ON public.audit_logs USING btree (action);


--
-- Name: idx_audit_date; Type: INDEX; Schema: public; Owner: pelican_user
--

CREATE INDEX idx_audit_date ON public.audit_logs USING btree (created_at);


--
-- Name: idx_audit_user; Type: INDEX; Schema: public; Owner: pelican_user
--

CREATE INDEX idx_audit_user ON public.audit_logs USING btree (user_email);


--
-- Name: idx_courrier_assigne; Type: INDEX; Schema: public; Owner: pelican_user
--

CREATE INDEX idx_courrier_assigne ON public.courriers USING btree (assigne_a_id);


--
-- Name: idx_courrier_createur; Type: INDEX; Schema: public; Owner: pelican_user
--

CREATE INDEX idx_courrier_createur ON public.courriers USING btree (createur_id);


--
-- Name: idx_courrier_statut; Type: INDEX; Schema: public; Owner: pelican_user
--

CREATE INDEX idx_courrier_statut ON public.courriers USING btree (statut);


--
-- Name: idx_courrier_type; Type: INDEX; Schema: public; Owner: pelican_user
--

CREATE INDEX idx_courrier_type ON public.courriers USING btree (type);


--
-- Name: idx_hist_courrier; Type: INDEX; Schema: public; Owner: pelican_user
--

CREATE INDEX idx_hist_courrier ON public.historique_courriers USING btree (courrier_id);


--
-- Name: idx_hist_date; Type: INDEX; Schema: public; Owner: pelican_user
--

CREATE INDEX idx_hist_date ON public.historique_courriers USING btree (date);


--
-- Name: idx_hist_user; Type: INDEX; Schema: public; Owner: pelican_user
--

CREATE INDEX idx_hist_user ON public.historique_courriers USING btree (user_id);


--
-- Name: idx_notif_date; Type: INDEX; Schema: public; Owner: pelican_user
--

CREATE INDEX idx_notif_date ON public.notifications USING btree (created_at);


--
-- Name: idx_notif_read; Type: INDEX; Schema: public; Owner: pelican_user
--

CREATE INDEX idx_notif_read ON public.notifications USING btree (is_read);


--
-- Name: idx_notif_user; Type: INDEX; Schema: public; Owner: pelican_user
--

CREATE INDEX idx_notif_user ON public.notifications USING btree (user_id);


--
-- Name: historique_courriers fk58k79ahok33cm9sfjh1umip8s; Type: FK CONSTRAINT; Schema: public; Owner: pelican_user
--

ALTER TABLE ONLY public.historique_courriers
    ADD CONSTRAINT fk58k79ahok33cm9sfjh1umip8s FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: notifications fk9y21adhxn0ayjhfocscqox7bh; Type: FK CONSTRAINT; Schema: public; Owner: pelican_user
--

ALTER TABLE ONLY public.notifications
    ADD CONSTRAINT fk9y21adhxn0ayjhfocscqox7bh FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: courriers fkcsp0nwwfx20uc6i3g0ix9tqry; Type: FK CONSTRAINT; Schema: public; Owner: pelican_user
--

ALTER TABLE ONLY public.courriers
    ADD CONSTRAINT fkcsp0nwwfx20uc6i3g0ix9tqry FOREIGN KEY (assigne_a_id) REFERENCES public.users(id);


--
-- Name: courriers fko337xilv47fl8kb5ivn1w4ggo; Type: FK CONSTRAINT; Schema: public; Owner: pelican_user
--

ALTER TABLE ONLY public.courriers
    ADD CONSTRAINT fko337xilv47fl8kb5ivn1w4ggo FOREIGN KEY (createur_id) REFERENCES public.users(id);


--
-- Name: historique_courriers fkrhpxk4qbofntrp7rvmakd40ao; Type: FK CONSTRAINT; Schema: public; Owner: pelican_user
--

ALTER TABLE ONLY public.historique_courriers
    ADD CONSTRAINT fkrhpxk4qbofntrp7rvmakd40ao FOREIGN KEY (courrier_id) REFERENCES public.courriers(id);


--
-- PostgreSQL database dump complete
--

\unrestrict UHZoIfxe8mfb3JqW4BBPJ17ayq5TPShi0kykaaI1s3YQfAuHAPaLQAT8xSMQKR2

