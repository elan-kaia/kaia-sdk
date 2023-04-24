package caver.sdk

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.media.ComposedSchema
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.responses.ApiResponse
import io.swagger.v3.oas.models.servers.Server
import org.openapitools.codegen.CodegenModel
import org.openapitools.codegen.CodegenOperation
import org.openapitools.codegen.CodegenProperty
import org.openapitools.codegen.languages.JavaClientCodegen
import org.openapitools.codegen.model.ModelsMap
import org.openapitools.codegen.utils.ModelUtils
import org.openapitools.codegen.CodegenResponse
import org.openapitools.codegen.model.OperationsMap;
import org.openapitools.codegen.model.ModelMap;
import org.openapitools.codegen.SupportingFile
import java.io.File

class KlaytnJavaClientCodegen : JavaClientCodegen {
    companion object {
        val caverName = "caver-java"
    }

    constructor() : super() {
    }

    override fun getName(): String {
        return caverName
    }

    override fun processOpts() {
        super.processOpts()
        supportingFiles.find { it -> it.templateFile.equals("build.gradle.mustache") }
        val modelFolder = (sourceFolder + File.separator + modelPackage).replace(".", "/")
        supportingFiles.add(SupportingFile("KlayGetAccountKey.java.mustache", modelFolder, "KlayGetAccountKey.java"))
    }

    override fun getUseInlineModelResolver(): Boolean {
        return true
    }

    override fun fromOperation(
        path: String,
        httpMethod: String,
        operation: Operation,
        servers: List<Server>?
    ): CodegenOperation {
        val op = super.fromOperation(path, httpMethod, operation, servers)
        val removedImports: MutableSet<String> = HashSet()
        for (name in op.imports) {
            if (name.contains("OneOfdoublestring") || name.contains("OneOfnumberstring")) {
                removedImports.add(name)
            }
        }
        for (name in removedImports) {
            op.imports.remove(name)
        }
        for (queryParam in op.queryParams) {
            if(queryParam.paramName.contains("OrTag")) {
                queryParam.vendorExtensions.put("x-default-latest", true)
            }
        }
        return op
    }

    override fun postProcessAllModels(objs: Map<String, ModelsMap>): Map<String, ModelsMap> {
        val result = super.postProcessAllModels(objs)
        val removedModel: MutableSet<String> = HashSet()
        for (entry in result.values) {
            for (mo in entry.models) {
                for (name in mo.model.imports) {
                    if (name.equals("Error")) {
                        removedModel.add(name)
                    }
                }
            }
        }
        for (name in removedModel) {
            result.remove(name)
        }
        return result
    }

    override fun preprocessOpenAPI(openAPI: OpenAPI?) {
        val oldKeys = ArrayList<String>()
        var namespace = String()
        if (openAPI?.tags?.size!! > 0) {
            namespace = openAPI.tags?.get(0)?.name!!
        }
        openAPI.components?.schemas?.toList()?.forEach { (t, u) ->
            if (t.contains("_200")) {
                val newKey = t.replace("_200", "").replaceFirst("", namespace.lowercase()+"_")
                oldKeys.add(t)
                u.addExtension("x-extend-response", newKey)
                openAPI.components?.schemas?.put(newKey, u)
            }
            if (t.contains("Resp_result")) {
                val newKey = t.replace("Resp_result", "")
                oldKeys.add(t)
                openAPI.components?.schemas?.put(newKey, u)
            }
            if (t.contains("_oneOf") || t.contains("_request") || t.contains("Req")) {
                oldKeys.add(t)
            }
        }
        openAPI.components?.schemas?.keys?.removeAll(oldKeys.toSet())

        if (useOneOfInterfaces && openAPI.components != null) {
            // we process the openapi schema here to find oneOf schemas and create interface models for them
            var schemas = openAPI.components.schemas;

            // we need to add all request and response bodies to processed schemas
            openAPI.paths.entries.forEach { (pathk, e) ->
                e.readOperationsMap().entries.forEach { (opk, op) ->
                    var opId = getOrGenerateOperationId(op, pathk, opk.toString());
                    // process request body
                    // ignore request schema because we don't use it

                    // process all response bodies
                    op.responses.forEach { (k, ar) ->
                        var a = ModelUtils.getReferencedApiResponse(openAPI, ar);
                        var responseSchema = ModelUtils.getSchemaFromResponse(a);
                        if (responseSchema != null) {
                            schemas.put(opId + k, responseSchema);
                        }
                    }
                }
            }

            // also add all properties of all schemas to be checked for oneOf
            var propertySchemas = HashMap<String, Schema<*>>();
            schemas.entries.forEach { (schk, s) ->
                s.properties.entries.forEach { (propk, p) ->
                    propertySchemas.put(schk + "/" + propk, p);
                }
            }
            schemas.putAll(propertySchemas)

            // go through all gathered schemas and add them as interfaces to be created
            schemas.entries.forEach { (k, s) ->
                var n = toModelName(k);
                var nOneOf = toModelName(n + "OneOf");
                if (ModelUtils.isComposedSchema(s)) {
                    if (k.contains("/")) {
                        // if this is property schema, we also need to generate the oneOf interface model
                        addOneOfNameExtension(s as? ComposedSchema, nOneOf);
                        addOneOfInterfaceModel(s as? ComposedSchema, nOneOf, openAPI);
                    } else {
                        // else this is a component schema, so we will just use that as the oneOf interface model
                        addOneOfNameExtension(s as? ComposedSchema, n);
                    }
                } else if (ModelUtils.isArraySchema(s as? ComposedSchema)) {
                    var items = (s).getItems();
                    if (ModelUtils.isComposedSchema(items)) {
                        addOneOfNameExtension(items as? ComposedSchema, nOneOf);
                        addOneOfInterfaceModel(items as? ComposedSchema, nOneOf, openAPI);
                    }
                } else if (ModelUtils.isMapSchema(s as? ComposedSchema)) {
                    var addProps = getAdditionalProperties(s as? ComposedSchema);
                    if (addProps != null && ModelUtils.isComposedSchema(addProps)) {
                        addOneOfNameExtension(addProps as? ComposedSchema, nOneOf);
                        addOneOfInterfaceModel(addProps as? ComposedSchema, nOneOf, openAPI);
                    }
                }
            }
        }

        // super.preprocessOpenAPI(openAPI)
    }

    override fun addImportsForPropertyType(model: CodegenModel?, property: CodegenProperty?) {
        val removedImports: MutableSet<String> = HashSet()
        model?.imports?.forEach {
            if (it?.contains("Error") == true) {
                removedImports.add(it)
            }
        }
        model?.imports?.removeAll(removedImports)
        super.addImportsForPropertyType(model, property)
    }

    override fun addImport(
        composed: ComposedSchema?,
        childSchema: Schema<*>?,
        model: CodegenModel?,
        modelName: String?
    ) {
        if (composed?.allOf != null && childSchema != null && childSchema.discriminator == null) {
            addImport(model, modelName)
        } else {
            println("Skipped import for allOf composition schema {}$modelName")
        }
    }

    override fun handleMethodResponse(
        operation: Operation?,
        schemas: MutableMap<String, Schema<Any>>?,
        op: CodegenOperation?,
        methodResponse: ApiResponse?,
        schemaMappings: MutableMap<String, String>?
    ) {

        var namespace = String()
        if (openAPI?.tags?.size!! > 0) {
            namespace = openAPI.tags?.get(0)?.name!!
        }
        val schema = methodResponse?.content?.values
        schema?.forEach {
            if (it?.schema?.`$ref`?.contains("_200") == true) {
                val newRef = it.schema?.`$ref`?.replace("_200", "")
                it.schema?.`$ref` = newRef?.
                replaceAfterLast("/", namespace.lowercase()+"_"+newRef.substringAfterLast("/"), "")
            }
        }
        super.handleMethodResponse(operation, schemas, op, methodResponse, schemaMappings)
    }

    override fun postProcessModelProperty(model: CodegenModel?, property: CodegenProperty?) {
        var namespace = String()
        if (openAPI?.tags?.size!! > 0) {
            namespace = openAPI.tags?.get(0)?.name!!
        }
        if (property?.ref?.contains("_200") == true) {
            val newRef = property.ref!!.replace("_200", "")
            property.ref = newRef.
            replaceAfterLast("/", namespace.lowercase()+"_"+newRef.substringAfterLast("/"), "")
        }
        if (property?.dataType?.contains("200") == true) {
            val newDataType = property.dataType!!.replace("200", "")
            property.dataType = newDataType.replaceBefore("", namespace.capitalize())
        }
        if (property?.datatypeWithEnum?.contains("200") == true) {
            val newDatatypeWithEnum = property.datatypeWithEnum!!.replace("200", "")
            property.datatypeWithEnum = newDatatypeWithEnum.replaceBefore("", namespace.capitalize())
        }
        if (property?.ref?.contains("Resp_result") == true) {
            val newRef = property.ref!!.replace("Resp_result", "")
            property.ref = newRef.
            replaceAfterLast("/", newRef.substringAfterLast("/"), "")
        }
        if (property?.dataType?.contains("RespResult") == true) {
            property.dataType = property.dataType!!.replace("RespResult", "")
        }
        if (property?.datatypeWithEnum?.contains("RespResult") == true) {
            property.datatypeWithEnum = property.datatypeWithEnum!!.replace("RespResult", "")
        }
        
        model?.imports?.forEach {
            if (it?.contains("200") == true) {
                model.imports?.remove(it)
            }
        }
        super.postProcessModelProperty(model, property)
    }
}
