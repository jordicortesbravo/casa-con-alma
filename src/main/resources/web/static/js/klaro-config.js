var klaroConfig = {
    // Idioma principal
    lang: 'es',

    // Apariencia del banner
    styling: {
        theme: ['dark', 'bottom', 'wide'],
    },

    // Propiedades generales
    version: 1,
    acceptAll: true,
    rejectAll: false,
    hideDeclineAll: false,
    mustConsent: false,
    acceptSelected: true,

    // Textos en español
    translations: {
        es: {
            consentModal: {
                title: 'Este sitio utiliza cookies',
                description:
                    'Usamos cookies para mejorar la experiencia del usuario y analizar el tráfico de nuestro sitio. Puedes seleccionar las cookies que deseas aceptar.',
            },
            consentNotice: {
                description:
                    'Este sitio utiliza cookies para ofrecer una mejor experiencia de navegación. Puedes elegir los servicios que deseas activar.',
                learnMore: 'Más información',
            },
            purposes: {
                analytics: 'Analítica',
                ads: 'Publicidad personalizada',
                affiliates: 'Enlaces de afiliados',
                newsletter: 'Suscripciones a newsletters',
                social: 'Redes Sociales',
                tagmanager: 'Gestión de etiquetas',
            },
            ok: 'Aceptar',
            acceptAll: 'Aceptar todas',
            save: 'Guardar',
            close: 'Cerrar',
            decline: 'Rechazar',
        },
    },

    // Servicios configurados
    services: [
        {
            name: 'google-analytics',
            title: 'Google Analytics',
            purposes: ['analytics'],
            default: true,
            cookies: ['_ga', '_gid', '_gat'],
            onAccept: function() {
                // Inserta dinámicamente el script de Google Tag Manager
                var gtmScript = document.createElement('script');
                gtmScript.src = 'https://www.googletagmanager.com/gtag/js?id=G-YZBBH93Q9Y';
                gtmScript.async = true;
                document.head.appendChild(gtmScript);

                gtmScript.onload = function() {
                    window.dataLayer = window.dataLayer || [];
                    function gtag() { dataLayer.push(arguments); }
                    gtag('js', new Date());
                    gtag('config', 'G-YZBBH93Q9Y');
                }
            },
            onDecline: function() {
                // Opcionalmente, limpia las cookies de Analytics si ya se crearon antes de rechazar
                document.cookie = "_ga=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
            },
            required: false,
            optOut: false,
            onlyOnce: true,
            description: 'Herramienta de analítica para mejorar la experiencia del usuario.',
        },
        {
            name: 'google-adsense',
            title: 'Google AdSense',
            purposes: ['ads'],
            default: true,
            cookies: ['_gads', '_gac_*'],
            required: false,
            optOut: false,
            onlyOnce: true,
            description: 'Publicidad personalizada proporcionada por Google.',
        },
        {
            name: 'affiliate-links',
            title: 'Enlaces de Afiliados',
            purposes: ['affiliates'],
            default: true,
            required: false,
            optOut: false,
            onlyOnce: true,
            description: 'Cookies de seguimiento para enlaces de afiliados que nos ayudan a obtener comisiones.',
        },
        {
            name: 'newsletter-subscription',
            title: 'Suscripción a Newsletters',
            purposes: ['newsletter'],
            default: true,
            required: false,
            optOut: false,
            onlyOnce: true,
            description: 'Permiso para almacenar tu correo electrónico para enviarte newsletters.',
        },
        {
            name: 'social-sharing',
            title: 'Redes Sociales',
            purposes: ['social'],
            default: true,
            required: false,
            optOut: false,
            onlyOnce: true,
            description: 'Permite compartir contenido en redes sociales y puede almacenar cookies de seguimiento.',
        },
        {
            name: 'google-tag-manager',
            title: 'Google Tag Manager',
            purposes: ['tagmanager'],
            default: true,
            required: false,
            optOut: false,
            onlyOnce: true,
            description: 'Gestión de etiquetas y scripts de terceros a través de Google Tag Manager.',
        },
        {
            name: 'facebook-pixel',
            title: 'Facebook Pixel',
            purposes: ['ads', 'social'],
            default: true,
            cookies: ['_fbp'],
            required: false,
            optOut: false,
            onlyOnce: true,
            description: 'Píxel de seguimiento para retargeting y análisis de Facebook.',
        },
        {
            name: 'twitter-pixel',
            title: 'Twitter Pixel',
            purposes: ['ads', 'social'],
            default: true,
            cookies: ['_twq'],
            required: false,
            optOut: false,
            onlyOnce: true,
            description: 'Píxel de seguimiento para retargeting y análisis de Twitter.',
        },
        {
            name: 'instagram-tracking',
            title: 'Seguimiento de Instagram',
            purposes: ['ads', 'social'],
            default: true,
            cookies: ['_instatrk'],
            required: false,
            optOut: false,
            onlyOnce: true,
            description: 'Seguimiento y retargeting para usuarios de Instagram.',
        },
        {
            name: 'youtube',
            title: 'YouTube',
            purposes: ['social'],
            default: true,
            cookies: ['VISITOR_INFO1_LIVE', 'YSC', 'PREF'],
            required: false,
            optOut: false,
            onlyOnce: true,
            description: 'Permite incrustar y reproducir vídeos de YouTube.',
        },
        {
            name: 'pinterest',
            title: 'Pinterest Tag',
            purposes: ['ads', 'social'],
            default: true,
            cookies: ['_pinterest_sess'],
            required: false,
            optOut: false,
            onlyOnce: true,
            description: 'Píxel de seguimiento para Pinterest para retargeting y análisis.',
        },
    ],
};
