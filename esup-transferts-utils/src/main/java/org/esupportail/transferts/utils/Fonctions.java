package org.esupportail.transferts.utils;

import artois.domain.beans.Interdit;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.transferts.domain.beans.DatasExterne;
import org.esupportail.transferts.domain.beans.OffreDeFormationsDTO;
import org.esupportail.transferts.domain.beans.Parametres;
import org.esupportail.transferts.domain.beans.User;
import org.springframework.util.ReflectionUtils;
import sun.net.www.protocol.http.AuthCacheImpl;
import sun.net.www.protocol.http.AuthCacheValue;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by farid on 29/03/2016.
 */
public class Fonctions {

    /**
     * A logger.
     */
    private static final Logger logger = new LoggerImpl(Fonctions.class);
    public static final String MSG_ERREURS = "Le service est indisponible, aucune vérification n'est possible pour le moment.";

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

    public static List<String> stringSplitToArrayList(String stringAsSplit, String split)
    {
        List<String> listString = new ArrayList<String>();
        if(stringAsSplit!=null && stringAsSplit!="" && ((stringAsSplit.split(split)).length>1))
        {
            String[] tokens = stringAsSplit.split(split);
            for(int i=0; i<tokens.length; i++)
                listString.add(tokens[i]);
        }
        else
            listString.add(stringAsSplit);

        return listString;
    }

    public static Class[] verifTypeOfClass(Object... params)
    {
        Class t[] = new Class[params.length];
        for(int i=0; i<params.length; i++)
        {
            if(params[i] instanceof Timestamp)
                t[i]= Date.class;
            else
                t[i]=params[i].getClass();

            logger.debug("Class t[]-->" + t);
            logger.debug("Class t[] ## " + i + " ##");
            logger.debug("Class t[]-->" + t[i].toString());
            logger.debug("Class t[]-->" + t[i].getName());
            logger.debug("Class t[]-->" + t[i].getClass());
        }
        return t;
    }

    public static Object[] appelWSOdfPartenaires2(String wsUrl, String wsLogin, String wsPwd, String maClass, String maMethode, Integer timeOutConnexionWs, Object... params)
    {
        Authenticator.setDefault(new MyAuthenticator(wsLogin, wsPwd));

        if (logger.isDebugEnabled()) {
            logger.debug("wsUrl===>" + wsUrl + "<===");
            logger.info("wsLogin===>" + wsLogin + "<===");
            logger.info("wsPwd===>" + wsPwd + "<===");
        }

        Object tabReturn[] = new Object[2];
        List<OffreDeFormationsDTO> odfs=new ArrayList<OffreDeFormationsDTO>();
        Integer online=0;

        Class cls=null;
        try {
            cls = Class.forName(maClass);
            if (logger.isDebugEnabled()){
                logger.debug("cls===>"+cls+"<===");
                logger.debug("params===>"+Arrays.toString(params)+"<===");
            }

            if (testUrl(wsUrl, timeOutConnexionWs))
            {
                if (logger.isDebugEnabled())
                    logger.debug("WebServices.appelWSPartenaires");
                try {
                    String address = wsUrl;
                    JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
                    factoryBean.setServiceClass(cls);
                    factoryBean.setAddress(address);
                    Object monService = factoryBean.create();

                    Class t[]=verifTypeOfClass(params);

                    Method m = ReflectionUtils.findMethod(cls, maMethode, t);

                    if (logger.isDebugEnabled()) {
                        logger.debug("WebServices.monService-->" + monService);
                        logger.debug("maMethode-->" + maMethode);
                        logger.debug("Method m===>"+m+"<===");
                    }

                    odfs = (List<OffreDeFormationsDTO>) ReflectionUtils.invokeMethod(m, monService, params);

                    logger.info("odfs===>"+odfs+"<===");

                    online=1;
                }
                catch (Exception e)
                {
                    logger.error(e);
                }
            }
            else
            {
                int codeErr = codeErreurHttp(wsUrl, timeOutConnexionWs);
                if (logger.isDebugEnabled())
                    logger.debug("codeErr===>"+codeErr+"<===");
            }

        }
        catch(Exception e )
        {
            logger.error(e);
        }
        AuthCacheValue.setAuthCache(new AuthCacheImpl());
        Authenticator.setDefault(null);

        tabReturn[0]=odfs;
        tabReturn[1]=online;
        return tabReturn;
    }

    public static Object[] appelWSAuth(String wsUrl, String wsLogin, String wsPwd, String maClass, String maMethode, String collection, Integer timeOutConnexionWs, Object... params)
    {
        logger.fatal("===>appelWSAuth<===");
        Authenticator.setDefault(new MyAuthenticator(wsLogin, wsPwd));

//        if (logger.isDebugEnabled()) {
            logger.info("wsUrl===>" + wsUrl + "<===");
            logger.info("wsLogin===>" + wsLogin + "<===");
            logger.info("wsPwd===>" + wsPwd + "<===");
            logger.info("maClass===>" + maClass + "<===");
            logger.info("maMethode===>" + maMethode + "<===");
            logger.info("collection===>" + collection + "<===");
            logger.info("timeOutConnexionWs.toString()===>" + timeOutConnexionWs.toString() + "<===");
//        }

        Object tabReturn[] = new Object[2];
        List<Object> objectRetourList = new ArrayList<Object>();
        Object objectRetour = new Object();

        Integer online=0;

        Class cls=null;
        try {
            cls = Class.forName(maClass);
//            if (logger.isDebugEnabled()){
                logger.warn("cls===>"+cls+"<===");
//                logger.debug("clsRetour===>"+clsRetour+"<===");
                logger.warn("params===>"+Arrays.toString(params)+"<===");
//            }

            if (testUrl(wsUrl, timeOutConnexionWs))
            {
                if (logger.isDebugEnabled())
                    logger.debug("WebServices.appelWSPartenaires");
                try {
                    String address = wsUrl;
                    JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
                    factoryBean.setServiceClass(cls);
                    factoryBean.setAddress(address);
                    Object monService = factoryBean.create();

                    Class t[]=verifTypeOfClass(params);

                    Method m = ReflectionUtils.findMethod(cls, maMethode, t);

                    if (logger.isDebugEnabled()) {
                        logger.debug("WebServices.monService-->" + monService);
                        logger.debug("maMethode-->" + maMethode);
                        logger.debug("Method m===>"+m+"<===");
                    }

                    if(collection.equals("arrayList"))
                        try {
                            objectRetourList = (List<Object>) ReflectionUtils.invokeMethod(m, monService, params);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    else
                        objectRetour = (Object) ReflectionUtils.invokeMethod(m, monService, params);

                    logger.info("objectRetourList===>"+objectRetourList+"<===");
                    logger.info("objectRetour===>"+objectRetour+"<===");

                    online=1;
                }
                catch (Exception e)
                {
                    logger.error(e);
                }
            }
            else
            {
                int codeErr = codeErreurHttp(wsUrl, timeOutConnexionWs);
                if (logger.isDebugEnabled())
                    logger.debug("codeErr===>"+codeErr+"<===");
            }

        }
        catch(Exception e )
        {
            logger.error(e);
        }
        AuthCacheValue.setAuthCache(new AuthCacheImpl());
        Authenticator.setDefault(null);

        if(collection.equals("arrayList"))
            tabReturn[0]=objectRetourList;
        else
            tabReturn[0]=objectRetour;
        tabReturn[1]=online;
        return tabReturn;
    }

    public static Object[] soso(String maClass, Object... params)
    {
        Class cls=null;
        try {
            cls = Class.forName(maClass);
            logger.fatal("maClass===>"+maClass+"<===");
            logger.fatal("cls===>"+cls+"<===");
//            Parametres p =
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public static List<DatasExterne> appelWSDatasExterne2(String wsUrl, String wsLogin, String wsPwd, String maClass, String maMethode, Integer timeOutConnexionWs, String... params){
        /**
         * niveau de l'interdit
         * 1 blocage de la saisie de la demande de transfert (interdit BU)
         * 2 VAP (candidatures)
         * 3 APB (postbac)
         */
        List<DatasExterne> listeInterdit=new ArrayList<DatasExterne>();
        List<Interdit> lInterditsWs=new ArrayList<Interdit>();

        Authenticator.setDefault(new MyAuthenticator(wsLogin, wsPwd));

        if (logger.isDebugEnabled()) {
            logger.debug("wsUrl===>" + wsUrl + "<===");
            logger.debug("wsLogin===>" + wsLogin + "<===");
            logger.debug("wsPwd===>" + wsPwd + "<===");
        }

        Class cls=null;
        try {
            cls = Class.forName(maClass);

            if (logger.isDebugEnabled()){
                logger.debug("cls===>"+cls+"<===");
                logger.debug("params===>"+Arrays.toString(params)+"<===");
            }

            if (testUrl(wsUrl, timeOutConnexionWs))
            {
                if (logger.isDebugEnabled())
                    logger.debug("WebServices.appelWSDatasExterne");
                try {
                    String address = wsUrl;
                    JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
                    factoryBean.setServiceClass(cls);
                    factoryBean.setAddress(address);
                    Object monService = factoryBean.create();

                    Class t[]=verifTypeOfClass(params);

                    Method m = ReflectionUtils.findMethod(cls, maMethode, t);

//                    if (logger.isDebugEnabled()) {
                        logger.info("WebServices.monService-->" + monService);
                        logger.info("maMethode-->" + maMethode);
                        logger.info("Method m===>"+m+"<===");
//                    }

                    try
                    {
                        lInterditsWs = (List<Interdit>) ReflectionUtils.invokeMethod(m, monService, params);

                        if(lInterditsWs!=null)
                        {
                            listeInterdit = new ArrayList<DatasExterne>();
                            for(Interdit c : lInterditsWs)
                            {
                                if (logger.isDebugEnabled()) {
                                    logger.debug("WebServices.Interdits===>" + c.getIdentifiant() + "<===");
                                    logger.debug("WebServices.Interdits===>" + c.getLibInterdit() + "<===");
                                }
                                DatasExterne de = new DatasExterne();
                                de.setCode(c.getSource());
                                de.setIdentifiant(c.getIdentifiant());
                                de.setNiveau(c.getCodeNiveauInterdit());
                                de.setLibInterdit(c.getLibInterdit());
                                listeInterdit.add(de);
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        logger.error(e);
                        DatasExterne de = new DatasExterne();
                        de.setCode("Erreur");
                        de.setIdentifiant("identifiant");
                        de.setNiveau(4);
                        de.setLibInterdit(MSG_ERREURS);
                        listeInterdit.add(de);
                     }
                }
                catch (Exception e)
                {
                    logger.error(e);
                    String summary = "ERREUR.ACCES_WS";
                    String detail = "ERREUR.ACCES_WS";
                    FacesMessage.Severity severity = FacesMessage.SEVERITY_ERROR;
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
                    DatasExterne de = new DatasExterne();
                    de.setCode("Erreur");
                    de.setIdentifiant("identifiant");
                    de.setNiveau(4);
                    de.setLibInterdit(MSG_ERREURS);
                    listeInterdit.add(de);
                }
            }
            else
            {
                int codeErr = codeErreurHttp(wsUrl, timeOutConnexionWs);
                if (logger.isDebugEnabled())
                    logger.debug("codeErr===>"+codeErr+"<===");
                String summary = "Erreur d'acces au Webservice : "+codeErr;
                String detail = "Erreur d'acces au Webservice : "+codeErr;
                FacesMessage.Severity severity = FacesMessage.SEVERITY_ERROR;
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
                DatasExterne de = new DatasExterne();
                de.setCode("Erreur");
                de.setIdentifiant("identifiant");
                de.setNiveau(4);
                de.setLibInterdit(MSG_ERREURS);
                listeInterdit.add(de);
            }

        }
        catch(Exception e )
        {
            logger.error(e);
            DatasExterne de = new DatasExterne();
            de.setCode("Erreur");
            de.setIdentifiant("identifiant");
            de.setNiveau(4);
            de.setLibInterdit(MSG_ERREURS);
            listeInterdit.add(de);
        }
        AuthCacheValue.setAuthCache(new AuthCacheImpl());
        Authenticator.setDefault(null);
        return listeInterdit;
    }

    public static boolean testUrl(String host, Integer timeOutConnexionWs) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(host).openConnection();
            conn.setConnectTimeout(timeOutConnexionWs);
            conn.connect();
            return conn.getResponseCode() == HttpURLConnection.HTTP_OK;
        } catch (MalformedURLException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("MalformedURLException");
                logger.debug("host : " + host);
            }
            logger.error(e);
            return false;
        } catch (IOException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("IOException");
                logger.debug("host : " + host);
            }
            logger.error(e);
            return false;
        }
    }

    private static int codeErreurHttp(String host, Integer timeOutConnexionWs) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(host).openConnection();
            conn.setConnectTimeout(timeOutConnexionWs);
            conn.connect();
            return conn.getResponseCode();
        } catch (MalformedURLException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("MalformedURLException");
                logger.debug("host : " + host);
            }
            logger.error(e);
            return 0;
        } catch (IOException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("IOException");
                logger.debug("host : " + host);
            }
            logger.error(e);
            return 0;
        }
    }
}
