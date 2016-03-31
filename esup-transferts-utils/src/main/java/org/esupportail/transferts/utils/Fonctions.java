package org.esupportail.transferts.utils;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by farid on 29/03/2016.
 */
public class Fonctions {

    /**
     * A logger.
     */
    private static final Logger logger = new LoggerImpl(Fonctions.class);

    public static Map<String, String> stringSplitToMap(String stringAsSplit, String split, boolean actif)
    {
        if(stringAsSplit!=null)
            if (logger.isDebugEnabled())
                logger.debug("Etablissement ajouté manuellement===>"+stringAsSplit+"<===");

        String[] tokens=null;
        Map <String, String> listEtabAjouteManuellement = new HashMap<String, String>();

        if(stringAsSplit!=null && stringAsSplit!="" && actif && ((stringAsSplit.split(split)).length>1))
        {
            tokens = stringAsSplit.split(split);
            for(int i=0; i<tokens.length; i++)
            {
                if (logger.isDebugEnabled())
                    logger.debug("tokens[n]===>"+tokens[i]+"<===");
                try{
                    listEtabAjouteManuellement.put(tokens[i].substring(0,tokens[i].indexOf('@')),tokens[i].substring(tokens[i].indexOf('@')+1));
                }catch(Exception e){
                    String summary = "Erreurs de syntaxe dans la liste des établissements ajoutés manuellements";
                    String detail = "Erreurs de syntaxe dans la liste des établissements ajoutés manuellements";
                    FacesMessage.Severity severity = FacesMessage.SEVERITY_WARN;
                    FacesContext context = FacesContext.getCurrentInstance();
                    context.addMessage(null, new FacesMessage(severity, summary, detail));
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    return null;
                }
            }
        }
        else if(stringAsSplit!=null && stringAsSplit!="" && actif && ((stringAsSplit.split(split)).length==1))
        {
            if (logger.isDebugEnabled())
                logger.debug("tokens===>"+stringAsSplit+"<===");
            try{
                listEtabAjouteManuellement.put(stringAsSplit.substring(0,stringAsSplit.indexOf('@')),stringAsSplit.substring(stringAsSplit.indexOf('@')+1));
            }catch(Exception e){
                String summary = "Erreurs de syntaxe dans la liste des établissements ajoutés manuellements";
                String detail = "Erreurs de syntaxe dans la liste des établissements ajoutés manuellements";
                FacesMessage.Severity severity = FacesMessage.SEVERITY_WARN;
                FacesContext context = FacesContext.getCurrentInstance();
                context.addMessage(null, new FacesMessage(severity, summary, detail));
                context.getExternalContext().getFlash().setKeepMessages(true);
                return null;
            }
        }
        else
            listEtabAjouteManuellement=null;

        return listEtabAjouteManuellement;
    }

}
