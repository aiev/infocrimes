from django.conf.urls import patterns, include, url
from django.contrib.gis import admin

from rest_framework import viewsets, routers

from infocrimes.apps.occurrence import views
from infocrimes import settings


# Uncomment the next two lines to enable the admin:
# from django.contrib import admin

admin.autodiscover()

router = routers.DefaultRouter()
router.register(r'facttypes', views.FactTypeViewSet)
router.register(r'facts', views.FactViewSet)


urlpatterns = patterns('',
    # Examples:
    # url(r'^$', 'geocrimes.views.home', name='home'),
    # url(r'^geocrimes/', include('geocrimes.foo.urls')),

    # Uncomment the admin/doc line below to enable admin documentation:
    # url(r'^admin/doc/', include('django.contrib.admindocs.urls')),

    # Uncomment the next line to enable the admin:
    url(r'^api/admin/', include('rest_framework.urls', namespace='rest_framework')),
    url(r'^admin/', include(admin.site.urls)),

    url(r'^api/', include(router.urls)),

    url(r'^(?P<path>.*)$', 'django.views.static.serve', {'document_root': settings.BASEDIR, 'show_indexes': True})
)
