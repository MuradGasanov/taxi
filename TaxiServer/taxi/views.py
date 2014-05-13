from django.http.response import HttpResponse
import models
import json


def settings(request):
    item = request.POST
    login_name = item.get("loginName", "")
    if not login_name:
        return HttpResponse("fail")
    driver, created = models.TaxiDriver.objects.get_or_create(login_name=login_name)
    driver.login_pin = item.get("loginPin", "")
    driver.full_name = item.get("fullName", "")
    driver.cab_name = item.get("cabName", "")
    driver.phone_number = item.get("phoneNumber", "")
    driver.max_pickup_distance = item.get("maxPickupDistance", "")
    driver.current_latitude = item.get("currentLatitude", 0.0)
    driver.current_longitude = item.get("currentLongitude", 0.0)
    driver.save()

    return HttpResponse(str(driver.id))


def get_or_zero(val):
    val = val if val else 0
    return float(val)


def submit(request):
    item = request.POST
    request_name = item.get("requestName", "")
    if not request_name:
        return HttpResponse("fail")
    rq, created = models.TaxiRequest.objects.get_or_create(request_name=request_name)
    rq.request_phone_number = item.get("requestPhoneNumber", "")
    rq.request_pickup_location = item.get("requestPickupLocation", "")
    rq.request_destination = item.get("requestDestination", "")
    rq.total_distance = get_or_zero(item.get("totalDistance"))
    rq.total_people = get_or_zero(item.get("totalPeople", 0))
    rq.current_latitude = get_or_zero(item.get("currentLatitude", 0))
    rq.current_longitude = get_or_zero(item.get("currentLongitude", 0))
    rq.to_latitude = get_or_zero(item.get("toLatitude", 0))
    rq.to_longitude = get_or_zero(item.get("toLongitude", 0))
    rq.assigned_driver_login = None
    rq.estimated_arrival_time = 0
    rq.save()

    return HttpResponse(str(rq.id))


def update(request):
    item = request.POST
    request_id = item.get("requestID", "")
    if not request_id:
        return HttpResponse("fail")
    rq = models.TaxiRequest.objects.get(id=int(request_id))
    assigned_driver = rq.assigned_driver_login
    rs = {
        "id": rq.id,
        "requestName": rq.request_name,
        "requestPhoneNumber": rq.request_phone_number,
        "requestPickupLocation": rq.request_pickup_location,
        "requestDestination": rq.request_destination,
        "assignedDriverLogin": assigned_driver.login_name if assigned_driver else "",
        "assignedDriverName": assigned_driver.full_name if assigned_driver else "",
        "assignedDriverPhoneNumber": assigned_driver.phone_number if assigned_driver else "",
        "assignedDriverLatitude": assigned_driver.current_latitude if assigned_driver else 0,
        "assignedDriverLongitude": assigned_driver.current_longitude if assigned_driver else 0,
        "estimatedArrivalTime": rq.estimated_arrival_time,
        "currentLatitude": rq.current_latitude,
        "currentLongitude": rq.current_longitude,
        "toLatitude": rq.to_latitude,
        "toLongitude": rq.to_longitude,
        "totalDistance": rq.total_distance,
        "totalPeople": rq.total_people,
        "isRequestTaken": "Y" if rq.is_request_taken else "N",
        "isRequestCompleted": "Y" if rq.is_request_completed else "N"
    }

    if rq.is_request_completed:
        rq.delete()

    return HttpResponse(json.dumps(rs))


def remove(request):
    item = request.POST
    request_id = item.get("requestID", "")
    if not request_id:
        return HttpResponse("fail")
    rq = models.TaxiRequest.objects.get(id=int(request_id))
    rq.delete()

    return HttpResponse('done')


def get_list(request):
    items = models.TaxiRequest.objects.filter(is_request_taken=False)
    ls = []
    for rq in items:
        assigned_driver = rq.assigned_driver_login
        rs = {
            "id": rq.id,
            "requestName": rq.request_name,
            "requestPhoneNumber": rq.request_phone_number,
            "requestPickupLocation": rq.request_pickup_location,
            "requestDestination": rq.request_destination,
            "assignedDriverLogin": assigned_driver.login_name if assigned_driver else "",
            "assignedDriverName": assigned_driver.fullName if assigned_driver else "",
            "assignedDriverPhoneNumber": assigned_driver.phoneNumber if assigned_driver else "",
            "assignedDriverLatitude": assigned_driver.currentLatitude if assigned_driver else 0,
            "assignedDriverLongitude": assigned_driver.currentLongitude if assigned_driver else 0,
            "estimatedArrivalTime": rq.estimated_arrival_time,
            "currentLatitude": rq.current_latitude,
            "currentLongitude": rq.current_longitude,
            "toLatitude": rq.to_latitude,
            "toLongitude": rq.to_longitude,
            "totalDistance": rq.total_distance,
            "totalPeople": rq.total_people,
            "isRequestTaken": "Y" if rq.is_request_taken else "N",
            "isRequestCompleted": "Y" if rq.is_request_completed else "N"}
        ls.append(rs)

    return HttpResponse(json.dumps(ls))


def approve(request):
    item = request.POST

    driver_login_name = request.POST.get("driverLoginName", "")
    driver = models.TaxiDriver.objects.get(login_name=driver_login_name)
    driver.current_latitude = get_or_zero(item.get("driverLatitude", ""))
    driver.current_longitude = get_or_zero(item.get("driverLongitude", ""))
    driver.save()

    rq = models.TaxiRequest.objects.get(id=int(item.get("requestID")))
    rq.estimated_arrival_time = get_or_zero(item.get("estimatedArrivalTime", 0))
    rq.assigned_driver_login = driver
    rq.is_request_taken = True
    rq.save()

    return HttpResponse("success")


def cancel(request):
    item = request.POST
    request_id = item.get("requestID", 0)

    rq = models.TaxiRequest.objects.get(id=request_id)

    rq.estimated_arrival_time = 0
    rq.assigned_driver_login = None
    rq.is_request_taken = False
    rq.save()

    return HttpResponse("success")


def complete(request):
    item = request.POST
    request_id = item.get("requestID", 0)

    rq = models.TaxiRequest.objects.get(id=request_id)

    rq.is_request_completed = True
    rq.save()

    return HttpResponse("success")