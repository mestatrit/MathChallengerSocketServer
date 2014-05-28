package it.mathchallenger.server.communication;

public class ListaErrori {
	public final static int OK = 0,
			DEVI_ESSERE_LOGGATO = 1,
			SEI_LOGGATO = 2,
			INVALID_AUTHCODE = 3,
			VECCHIA_PASSWORD_ERRATA = 4,
			USERNAME_IN_USO = 5,
			NON_PUOI_SFIDARE_QUESTO_UTENTE = 6,
			NON_E_UNA_TUA_PARTITA = 7, 
			PARTITA_NON_TROVATA = 8,
			RIPROVA_PIU_TARDI = 9,
			NON_PUOI_AGGIUNGERE_QUESTO_ACCOUNT_AGLI_AMICI = 10,
			VERSIONE_NON_VALIDA = 11;
}
