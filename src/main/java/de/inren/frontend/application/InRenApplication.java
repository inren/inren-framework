/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package de.inren.frontend.application;

import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;

import org.apache.wicket.Component;
import org.apache.wicket.Localizer;
import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authroles.authorization.strategies.role.metadata.MetaDataRoleAuthorizationStrategy;
import org.apache.wicket.devutils.inspector.RenderPerformanceListener;
import org.apache.wicket.markup.html.IPackageResourceGuard;
import org.apache.wicket.markup.html.SecurePackageResourceGuard;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.request.WebClientInfo;
import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.IRequestMapper;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.wicketstuff.annotation.scan.AnnotatedMountScanner;

import de.agilecoders.wicket.core.Bootstrap;
import de.agilecoders.wicket.core.settings.BootstrapSettings;
import de.agilecoders.wicket.themes.markup.html.bootswatch.BootswatchTheme;
import de.agilecoders.wicket.themes.markup.html.bootswatch.BootswatchThemeProvider;
import de.inren.frontend.application.security.InRenAuthorizationStrategy;
import de.inren.frontend.common.dns.DnsUtil;
import de.inren.frontend.storehouse.picture.PictureResource;
import de.inren.security.BasicAuthenticationSession;
import de.inren.security.SignInPage;
import de.inren.service.Initializable;
import de.inren.service.security.ComponentAccessService;
import de.inren.service.user.UserService;

/**
 * 
 * @author Ingo Renner
 *
 */
public class InRenApplication extends AuthenticatedWebApplication {

    private final static Logger log                  = LoggerFactory.getLogger(InRenApplication.class);

    public static final String  THUMBNAIL_IMAGE_PATH = "thumb";
    public static final String  PREVIEW_IMAGE_PATH   = "perview";
    public static final String  LAYOUT_IMAGE_PATH    = "layout";
    public static final String  IMAGE_PAGE_PATH      = "image";

    @SpringBean
    UserService                 userService;

    @SpringBean
    ComponentAccessService      componentAccessService;

    private ApplicationStatus   applicationStatus    = new ApplicationStatus();

    public InRenApplication() {
        super();
    }

    public static InRenApplication get() {
        return (InRenApplication) WebApplication.get();
    }

    @Override
    public Class<? extends WebPage> getHomePage() {
        return HomePage.class;
    }

    @Override
    public void init() {
        super.init();

        /* Spring injection. */
        this.getComponentInstantiationListeners().add(new SpringComponentInjector(this));

        /* Performance measurement */
        if (false) {
            getComponentInstantiationListeners().add(new RenderPerformanceListener());
        }

        configureBootstrap();

        initializeServices();

        getSecuritySettings().setAuthorizationStrategy(new MetaDataRoleAuthorizationStrategy(this));

        initializeFailSafeLocalize();
        new AnnotatedMountScanner().scanPackage("de.inren").mount(this);

        mountResource("/" + PictureResource.PICTURE_RESOURCE + "/${" + PictureResource.ID + "}/${" + PictureResource.SIZE + "}", PictureResource.asReference());

        // Access to Images by url
        // mount(createImageURIRequestTargetUrlCodingStrategy());
        // mount(createLayoutURIRequestTargetUrlCodingStrategy());
        // mount(createThumbnailURIRequestTargetUrlCodingStrategy());

        // Render hints in html to navigate from firebug to eclipse
        // WicketSource.configure(this);

        // TODO gehört ins codeflower package. => Init für Wicket
        // module/packages machen.
        final IPackageResourceGuard packageResourceGuard = getResourceSettings().getPackageResourceGuard();
        if (packageResourceGuard instanceof SecurePackageResourceGuard) {
            ((SecurePackageResourceGuard) packageResourceGuard).addPattern("+*.json");
            ((SecurePackageResourceGuard) packageResourceGuard).addPattern("+**.ttf");
        }

        /** for ajax progressbar on upload */
        getApplicationSettings().setUploadProgressUpdatesEnabled(true);

        // I don't like messy html code.
        this.getMarkupSettings().setStripWicketTags(true);
        this.getMarkupSettings().setStripComments(true);

        // This application can be reached from internet, but you must know the
        // right port.
        // So I like to know who else besides me tries to connect.

        getRequestCycleListeners().add(new AbstractRequestCycleListener() {
            @Override
            public void onBeginRequest(RequestCycle cycle) {
                WebClientInfo ci = new WebClientInfo(cycle);
                log.debug("Request info: "
                        + ci.getProperties().getRemoteAddress()
                        + ", "
                        + ("".equals(DnsUtil.lookup(ci.getProperties().getRemoteAddress())) ? "" : DnsUtil.lookup(ci.getProperties().getRemoteAddress()) + ", ")
                        + (cycle.getRequest().getUrl().getPath() == null || "".equals(cycle.getRequest().getUrl().getPath()) ? "" : cycle.getRequest().getUrl()
                                .getPath()
                                + ", ") + ci.getUserAgent());
            }
        });

        getSecuritySettings().setAuthorizationStrategy(new InRenAuthorizationStrategy());

        // MetaDataRoleAuthorizationStrategy.authorize(AdminOnlyPage.class,
        // Roles.ADMIN);
        // mountPage("admin", AdminOnlyPage.class);

        // TODO das funzzt nur auf Class

        // LIST<COMPONENTACCESS> COMPONENTACCESS =
        // COMPONENTACCESSSERVICE.GETCOMPONENTACCESSLIST();
        // FOR (COMPONENTACCESS CA : COMPONENTACCESS) {
        // COLLECTION<ROLE> ROLES = CA.GETGRANTEDROLES();
        // STRINGBUFFER ROLESTRING = NEW STRINGBUFFER();
        // FOR (ROLE ROLE : ROLES) {
        // ROLESTRING.APPEND(ROLE);
        // }
        // METADATAROLEAUTHORIZATIONSTRATEGY.AUTHORIZE(CA.GETNAME(),
        // ROLESTRING.TOSTRING());
        // }
    }

    /**
     * Collect all services by spring and let them initialize. After that, we
     * have a clean environment to work with.
     **/
    protected void initializeServices() {
        Map<String, Initializable> services = getApplicationContext().getBeansOfType(Initializable.class);
        log.info("Services = " + (services == null ? "no services found." : services.keySet()));

        if (services != null) {
            for (Initializable service : services.values()) {
                try {
                    service.init();
                } catch (Exception e) {
                    log.error("failed to initialize service", e);
                }
            }
        }
    }

    private ApplicationContext getApplicationContext() {
        return WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
    }

    @Override
    protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
        return BasicAuthenticationSession.class;
    }

    @Override
    public Session newSession(final Request request, final Response response) {
        Session session = super.newSession(request, response);
        applicationStatus.addSession(session);
        return session;
    }

    @Override
    protected Class<? extends WebPage> getSignInPageClass() {
        return SignInPage.class;
    }

    private void configureBootstrap() {
        BootstrapSettings settings = new BootstrapSettings();
        settings.setThemeProvider(new BootswatchThemeProvider(BootswatchTheme.Journal));
        Bootstrap.install(this, settings);

    }

    /**
     * I'm lazy some times. Don't throw exceptions when properties are missing,
     * just remind me with a default value (the key+locale).
     */
    private void initializeFailSafeLocalize() {
        Localizer localizer = new Localizer() {

            @Override
            public String getString(String key, Component component, IModel<?> model, Locale locale, String style, String defaultValue)
                    throws MissingResourceException {
                try {
                    return super.getString(key, component, model, locale, style, defaultValue);
                } catch (MissingResourceException e) {
                    String variation = (component != null ? component.getVariation() : null);
                    String cacheKey = getCacheKey(key, component, locale, style, variation);
                    log.info("######### Missing: cacheKey=[" + cacheKey + "] " + e.getMessage());
                    final String text = key + (locale == null ? "" : locale.getLanguage());
                    return "[" + text + "]";
                }
            }

        };
        this.getResourceSettings().setLocalizer(localizer);
    }

    public FeedbackPanel getFeedbackPanel(Page page) {
        return (FeedbackPanel) page.visitChildren(new FeedbackPanelVisitor());
    }

    public String getThumbnailUrl(String id, RequestCycle rc) {
        return createPicteResourceUrl(rc, id, THUMBNAIL_IMAGE_PATH);
    }

    public String getPreviewUrl(String id, RequestCycle rc) {
        return createPicteResourceUrl(rc, id, PREVIEW_IMAGE_PATH);
    }

    public String getLayoutUrl(String id, RequestCycle rc) {
        return createPicteResourceUrl(rc, id, LAYOUT_IMAGE_PATH);
    }

    private String createPicteResourceUrl(RequestCycle rc, String id, String size) {
        return rc.getRequest().getContextPath() + "/" + PictureResource.PICTURE_RESOURCE + "/" + id + "/" + size;
    }

    private IRequestMapper createImageURIRequestTargetUrlCodingStrategy() {

        IRequestMapper mapper = new IRequestMapper() {

            @Override
            public IRequestHandler mapRequest(Request request) {
                IRequestHandler handler = new IRequestHandler() {

                    @Override
                    public void respond(IRequestCycle requestCycle) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void detach(IRequestCycle requestCycle) {
                        // TODO Auto-generated method stub

                    }
                };
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public int getCompatibilityScore(Request request) {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public Url mapHandler(IRequestHandler requestHandler) {
                // TODO Auto-generated method stub
                return null;
            }
        };
        return mapper;
    }

    public ApplicationStatus getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(ApplicationStatus applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    // return new URIRequestTargetUrlCodingStrategy("/" + IMAGE_PAGE_PATH) {
    // @Override
    // public IRequestTarget decode(RequestParameters requestParameters) {
    // // Get request URI
    // String uri = getURI(requestParameters);
    // Long id;
    // try {
    // id = Long.valueOf(uri);
    // } catch (NumberFormatException e) {
    // id = 0L;
    // }
    // final File imageFile;
    // final String name;
    // if (id > 0L) {
    // Picture pic = pictureService.loadPicture(id);
    // imageFile = pictureModificationService.getImage(pic.getDigest());
    // name = pic.getFilename();
    // } else {
    // imageFile =
    // pictureModificationService.getImage(PictureModificationService.FILE_NOT_FOUND_DIGEST);
    // name = PictureModificationService.FILE_NOT_FOUND_NAME;
    // }
    // return new ImageResourceStreamRequestTarget(new FileResourceStream(new
    // org.apache.wicket.util.file.File(imageFile)), 0) {
    // @Override
    // public String getFileName() {
    // return name;
    // }
    // };
    // }
    //
    // @Override
    // public boolean matches(IRequestTarget requestTarget) {
    // if (requestTarget instanceof ImageResourceStreamRequestTarget) {
    // return ((ImageResourceStreamRequestTarget)
    // requestTarget).getImageTypeId() == 0;
    // }
    // return false;
    // }
    // };
    // }
    //
    // private URIRequestTargetUrlCodingStrategy
    // createLayoutURIRequestTargetUrlCodingStrategy() {
    // return new URIRequestTargetUrlCodingStrategy("/" + LAYOUT_IMAGE_PATH) {
    // @Override
    // public IRequestTarget decode(RequestParameters requestParameters) {
    // // Get request URI
    // String uri = getURI(requestParameters);
    // Long id;
    // try {
    // id = Long.valueOf(uri);
    // } catch (NumberFormatException e) {
    // id = 0L;
    // }
    // final File imageFile;
    // final String name;
    // if (id > 0L) {
    // Picture pic = pictureService.loadPicture(id);
    // imageFile = pictureModificationService.getLayoutImage(pic.getDigest());
    // name = pic.getFilename();
    // } else {
    // imageFile =
    // pictureModificationService.getLayoutImage(PictureModificationService.FILE_NOT_FOUND_DIGEST);
    // name = PictureModificationService.FILE_NOT_FOUND_NAME;
    // }
    // return new ImageResourceStreamRequestTarget(new FileResourceStream(new
    // org.apache.wicket.util.file.File(imageFile)), 1) {
    // @Override
    // public String getFileName() {
    // return name + ".png";
    // }
    // };
    // }
    //
    // @Override
    // public boolean matches(IRequestTarget requestTarget) {
    // if (requestTarget instanceof ImageResourceStreamRequestTarget) {
    // return ((ImageResourceStreamRequestTarget)
    // requestTarget).getImageTypeId() == 1;
    // }
    // return false;
    // }
    // };
    // }
    //
    // private URIRequestTargetUrlCodingStrategy
    // createThumbnailURIRequestTargetUrlCodingStrategy() {
    // return new URIRequestTargetUrlCodingStrategy("/" + THUMBNAIL_IMAGE_PATH)
    // {
    // @Override
    // public IRequestTarget decode(RequestParameters requestParameters) {
    // // Get request URI
    // String uri = getURI(requestParameters);
    // Long id;
    // try {
    // id = Long.valueOf(uri);
    // } catch (NumberFormatException e) {
    // id = 0L;
    // }
    // final File imageFile;
    // final String name;
    // if (id > 0L) {
    // Picture pic = pictureService.loadPicture(id);
    // imageFile =
    // pictureModificationService.getThumbnailImage(pic.getDigest());
    // name = pic.getFilename();
    // } else {
    // imageFile =
    // pictureModificationService.getThumbnailImage(PictureModificationService.FILE_NOT_FOUND_DIGEST);
    // name = PictureModificationService.FILE_NOT_FOUND_NAME;
    // }
    // return new ImageResourceStreamRequestTarget(new FileResourceStream(new
    // org.apache.wicket.util.file.File(imageFile)), 2) {
    // @Override
    // public String getFileName() {
    // return name + ".png";
    // }
    // };
    // }
    //
    // @Override
    // public boolean matches(IRequestTarget requestTarget) {
    // if (requestTarget instanceof ImageResourceStreamRequestTarget) {
    // return ((ImageResourceStreamRequestTarget)
    // requestTarget).getImageTypeId() == 2;
    // }
    // return false;
    // }
    // };
    // }

}
