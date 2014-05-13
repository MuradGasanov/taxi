from django.db import models


class TaxiDriver(models.Model):
    login_name = models.CharField(max_length=50)
    login_pin = models.CharField(max_length=50)
    full_name = models.CharField(max_length=50)
    cab_name = models.CharField(max_length=50)
    phone_number = models.CharField(max_length=20)
    current_latitude = models.FloatField(default=0)
    current_longitude = models.FloatField(default=0)
    max_pickup_distance = models.FloatField(default=0)


class TaxiRequest(models.Model):
    request_name = models.CharField(max_length=50)
    request_phone_number = models.CharField(max_length=20)
    request_pickup_location = models.CharField(max_length=50)
    request_destination = models.CharField(max_length=50)
    current_latitude = models.FloatField(default=0)
    current_longitude = models.FloatField(default=0)
    to_latitude = models.FloatField(default=0)
    to_longitude = models.FloatField(default=0)
    total_people = models.IntegerField(default=0)
    total_distance = models.FloatField(default=0)
    assigned_driver_login = models.ForeignKey(TaxiDriver, null=True)
    estimated_arrival_time = models.FloatField(default=0)
    is_request_taken = models.BooleanField(default=False)
    is_request_completed = models.BooleanField(default=False)