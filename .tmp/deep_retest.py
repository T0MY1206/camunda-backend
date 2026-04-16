import json
import urllib.error
import urllib.request

BASE = "http://127.0.0.1:18080/api/v1/camunda"


def req(path: str, method: str = "GET", body=None):
    data = None
    request = urllib.request.Request(BASE + path, method=method)
    request.add_header("Accept", "application/json")
    if body is not None:
        data = json.dumps(body).encode("utf-8")
        request.add_header("Content-Type", "application/json")
    try:
        with urllib.request.urlopen(request, data=data, timeout=20) as response:
            return response.status, response.read().decode("utf-8", "ignore")
    except urllib.error.HTTPError as err:
        return err.code, err.read().decode("utf-8", "ignore")


ctx = {}
status, body = req("/process-definition")
definitions = json.loads(body) if status == 200 else []
process_definition_id = definitions[0]["id"]
ctx["processDefinitionId"] = process_definition_id

status, body = req(
    f"/process-definition/{process_definition_id}/start",
    "POST",
    {"variables": {"smokeVar": {"value": "ok", "type": "String"}}},
)
process_instance_id = json.loads(body)["id"]
ctx["processInstanceId"] = process_instance_id

status, body = req(f"/execution?processInstanceId={process_instance_id}")
executions = json.loads(body) if status == 200 else []
ctx["executionId"] = executions[0]["id"] if executions else None

status, body = req(f"/task?processInstanceId={process_instance_id}")
tasks = json.loads(body) if status == 200 else []
ctx["taskId"] = tasks[0]["id"] if tasks else None

status, body = req("/deployment")
deployment_id = json.loads(body)[0]["id"]
ctx["deploymentId"] = deployment_id

status, body = req(f"/deployment/{deployment_id}/resources")
resources = json.loads(body) if status == 200 else []
ctx["resourceId"] = resources[0]["id"] if resources else None

req("/tenant/create", "POST", {"id": "tenant-a", "name": "Tenant A"})

status, body = req("/task/create", "POST", {"name": "tmp-delete-task"})
created_task_id = None
if status in (200, 201):
    try:
        created_task_id = json.loads(body).get("id")
    except Exception:
        created_task_id = None

if not created_task_id:
    status, body = req("/task?name=tmp-delete-task")
    if status == 200:
        listed = json.loads(body)
        if listed:
            created_task_id = listed[0].get("id")
ctx["deletableTaskId"] = created_task_id

checks = [
    ("GET", f"/deployment/{ctx['deploymentId']}/resources/{ctx['resourceId']}/data", None),
    ("GET", f"/execution/{ctx['executionId']}/localVariables", None),
    ("DELETE", f"/execution/{ctx['executionId']}/localVariables/smokeVar", None),
    ("GET", f"/execution/{ctx['executionId']}/localVariables/smokeVar", None),
    ("GET", f"/execution/{ctx['executionId']}/localVariables/smokeVar/data", None),
    ("PUT", "/group/camunda-admin/members/demo", None),
    ("GET", f"/process-instance/{ctx['processInstanceId']}/variables", None),
    ("DELETE", f"/process-instance/{ctx['processInstanceId']}/variables/smokeVar", None),
    ("GET", f"/process-instance/{ctx['processInstanceId']}/variables/smokeVar", None),
    ("GET", f"/process-instance/{ctx['processInstanceId']}/variables/smokeVar/data", None),
    (
        "DELETE",
        f"/task/{ctx['deletableTaskId']}" if ctx["deletableTaskId"] else "/task/dummy",
        None,
    ),
    ("PUT", "/tenant/tenant-a/group-members/camunda-admin", None),
    ("PUT", "/tenant/tenant-a/user-members/demo", None),
]

results = []
for method, path, payload in checks:
    status, body = req(path, method, payload)
    results.append(
        {
            "method": method,
            "path": path,
            "status": status,
            "ok": status < 500,
            "sample": body[:180],
        }
    )

report = {"context": ctx, "results": results}
print(json.dumps(report, indent=2))
with open("target/parity-deep-fail-retest.json", "w", encoding="utf-8") as handler:
    json.dump(report, handler, indent=2)
