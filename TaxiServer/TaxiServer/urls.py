from django.conf.urls import patterns, include, url

from django.contrib import admin
admin.autodiscover()

urlpatterns = patterns('',
    # url(r'^$', 'TaxiServer.views.home', name='home'),
    # url(r'^blog/', include('blog.urls')),

    url(r'^admin/', include(admin.site.urls)),
    url('settings', 'taxi.views.settings'),
    url('submit', 'taxi.views.submit'),
    url('update', 'taxi.views.update'),
    url('remove', 'taxi.views.remove'),
    url('list', 'taxi.views.get_list'),
    url('approve', 'taxi.views.approve'),
    url('cancel', 'taxi.views.cancel'),
    url('complete', 'taxi.views.complete')
)
