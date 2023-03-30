package com.github.sdp.mediato.model;

import com.github.sdp.mediato.model.media.Media;
import com.github.sdp.mediato.model.media.MediaType;

import java.util.ArrayList;
import java.util.List;

/**
 * for testing purposes
 * this class proposes a local set of all the james bond movies
 */
public class LocalFilmDatabase {

    private List<Media> filmItems;

    public LocalFilmDatabase() {
        this.filmItems = new ArrayList<>();

        filmItems.add(new Media(MediaType.MOVIE, "James Bond contre Dr No", " 1962 ", "js_007_contre_dr.jpeg", 0));
        filmItems.add(new Media(MediaType.MOVIE, "Bon Baisers de Russie ", " 1963 ", "bon_baiser_de_russie.jpeg", 0));
        filmItems.add(new Media(MediaType.MOVIE, " Goldfinger ", " 1964 ", "goldfinger.jpeg", 0));
        filmItems.add(new Media(MediaType.MOVIE, " Opération Tonnerre ", " 1965 ", "operation_tonnerre.jpeg", 0));
        filmItems.add(new Media(MediaType.MOVIE, " On ne vit que deux fois ", " 1967 ", "on_ne_vit_que_deux_fois.jpeg", 0));
        filmItems.add(new Media(MediaType.MOVIE, " Au service sercret de Sa Majesté ", " 1969 ", "au_service_secret_de_sa_majeste.jpeg", 0));
        filmItems.add(new Media(MediaType.MOVIE, " Les diaments sont éternels ", " 1971 ", "les_diamants_sont_eternels.jpeg", 0));
        filmItems.add(new Media(MediaType.MOVIE, " Vivre et laisser mourir ", " 1973 ", "vivre_et_laisser_mourir.jpeg", 0));
        filmItems.add(new Media(MediaType.MOVIE, " L\'homme au pistolet d\'or", " 1974 ", "l_homme_au_pistolet_d_or.jpeg", 0));
        filmItems.add(new Media(MediaType.MOVIE, "L\'espion qui m\'aimait", " 1977 ", "l_espion_qui_m_aimait.jpeg", 0));
        filmItems.add(new Media(MediaType.MOVIE, "Moonraker", " 1979 ", "moonraker.jpeg", 0));
        filmItems.add(new Media(MediaType.MOVIE, " Rien que pour vos yeux", " 1981 ", "rien_que_pour_vos_yeux.jpeg", 0));
        filmItems.add(new Media(MediaType.MOVIE, " Octopussy ", " 1983 ", "octopussy.jpeg", 0));
        filmItems.add(new Media(MediaType.MOVIE, " Jamais plus jamais", " 1983 ", "jamais_plus_jamais.jpeg", 0));
        filmItems.add(new Media(MediaType.MOVIE, " Dangereusement vôtre ", "1985", "dangereusement_votre.jpeg", 0));
        filmItems.add(new Media(MediaType.MOVIE, " Tuer n\'est pas jouer", " 1987 ", "tuer_nest_pas_jouer.jpeg", 0));
        filmItems.add(new Media(MediaType.MOVIE, " Permis de tuer", " 1989 ", "permis_de_tuer.jpeg", 0));
        filmItems.add(new Media(MediaType.MOVIE, " GoldenEye ", " 1995 ", "golden_eye.jpeg", 0));
        filmItems.add(new Media(MediaType.MOVIE, " Demain ne meurt jamais ", " 1997 ", "demain_ne_meurt_jamais.jpeg", 0));
        filmItems.add(new Media(MediaType.MOVIE, " Le monde ne suffit pas", " 1999 ", "le_monde_ne_suffit_pas.jpeg", 0));
        filmItems.add(new Media(MediaType.MOVIE, " Meurs un autre jour ", " 2002 ", "meurs_un_autre_jour.jpeg", 0));
        filmItems.add(new Media(MediaType.MOVIE, " Casino Royale ", " 2006 ", "casino_royal.jpeg", 0));
        filmItems.add(new Media(MediaType.MOVIE, " Quantum of Solace", "2008 ", "quantum_of_solace.jpeg", 0));
        filmItems.add(new Media(MediaType.MOVIE, " Skyfall ", " 2012 ", "skyfall.jpeg", 0));
        filmItems.add(new Media(MediaType.MOVIE, " 007 Spectre ", " 2015 ", "js_007_spectre.jpeg", 0));
        filmItems.add(new Media(MediaType.MOVIE, " Mourir peut attendre", " 2021 ", "mourir_peut_attendre.jpeg", 0));
    }

    public List<Media> getMovieItems() {
        return filmItems;
    }
}
