#!/bin/bash
set -e


read -p "Target module : " module
read -p "Uppercased (camel) new entity name : " entityUpperCased 
read -p "Plural uppercased (camel) new entity name : " entityPlural
read -p "Service name : " serviceName
read -p "Controller name lowercased : " controllerName
read -p "Entry point singular lowercased : " entryPointNameSingular
read -p "Entry point plural lowercased : " entryPointName

entityLowerCased="$(tr "[:upper:]" "[:lower:]" <<< ${entityUpperCased:0:1})${entityUpperCased:1}"
entityPluralLowerCased="$(tr "[:upper:]" "[:lower:]" <<< ${entityPlural:0:1})${entityPlural:1}"

moveAndTransform() {
	sourceFile=$1
	targetFile=$2
	cp $sourceFile $targetFile
	sed -i "s/NewEntity/$entityUpperCased/g" $targetFile
	sed -i "s/newEntity/$entityLowerCased/g" $targetFile
	sed -i "s/NewEntities/$entityPlural/g" $targetFile
	sed -i "s/targetPackage/$module/g" $targetFile
	sed -i "s/controllerName/$controllerName/g" $targetFile
	sed -i "s/entryPointNameSingular/$entryPointNameSingular/g" $targetFile
	sed -i "s/entryPointName/$entryPointName/g" $targetFile
	sed -i "s/entityPluralLowerCased/$entityPluralLowerCased/g" $targetFile
	sed -i "s/serviceName/$serviceName/g" $targetFile
}


moveAndTransform backend/service/NewEntityService.java ../../src/main/java/com/jss/osiris/modules/$module/service/${entityUpperCased}Service.java
moveAndTransform backend/service/NewEntityServiceImpl.java ../../src/main/java/com/jss/osiris/modules/$module/service/${entityUpperCased}ServiceImpl.java
moveAndTransform client/services/newentity.service.ts ../../client/src/app/modules/$module/services/${serviceName}.service.ts
mkdir -p ../../client/src/app/modules/administration/components/referentials/referential-${entryPointNameSingular}
moveAndTransform client/referential/referential-new-entity.component.ts ../../client/src/app/modules/administration/components/referentials/referential-${entryPointNameSingular}/referential-${entryPointNameSingular}.component.ts

echo "@Autowired"
echo  "${entityUpperCased}Service  ${entityLowerCased}Service;"
echo "@GetMapping(inputEntryPoint + \"/$entryPointName\")"
echo "public ResponseEntity<List<$entityUpperCased>> get$entityPlural() {"
echo "List<$entityUpperCased> $entityPluralLowerCased = null;"
echo "	try {"
echo "	$entityPluralLowerCased = ${entityLowerCased}Service.get$entityPlural();"
echo "	} catch (HttpStatusCodeException e) {"
echo "	logger.error(\"HTTP error when fetching $entityLowerCased\", e);"
echo "	return new ResponseEntity<List<$entityUpperCased>>(HttpStatus.INTERNAL_SERVER_ERROR);"
echo "	} catch (Exception e) {"
echo "	logger.error(\"Error when fetching $entityLowerCased\", e);"
echo "	return new ResponseEntity<List<$entityUpperCased>>(HttpStatus.INTERNAL_SERVER_ERROR);"
echo "	}"
echo "	return new ResponseEntity<List<$entityUpperCased>>($entityPluralLowerCased, HttpStatus.OK);"
echo "	}"

echo ""
echo ""

echo "@PostMapping(inputEntryPoint + \"/$entryPointNameSingular\")"
echo "public ResponseEntity<$entityUpperCased> addOrUpdate$entityUpperCased("
echo "    @RequestBody $entityUpperCased $entityPluralLowerCased) {"
echo "  $entityUpperCased out${entityUpperCased};"
echo "  try {"
echo "    if ($entityPluralLowerCased.getId() != null)"
echo "      validationHelper.validateReferential($entityPluralLowerCased, true);"
echo "    validationHelper.validateString($entityPluralLowerCased.getCode(), true);"
echo "    validationHelper.validateString($entityPluralLowerCased.getLabel(), true);"
echo ""
echo "    out${entityUpperCased} = ${entityLowerCased}Service"
echo "        .addOrUpdate$entityUpperCased($entityPluralLowerCased);"
echo "  } catch ("
echo ""
echo "  ResponseStatusException e) {"
echo "    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);"
echo "  } catch (HttpStatusCodeException e) {"
echo "    logger.error(\"HTTP error when fetching $entityLowerCased\", e);"
echo "    return new ResponseEntity<$entityUpperCased>(HttpStatus.INTERNAL_SERVER_ERROR);"
echo "  } catch (Exception e) {"
echo "    logger.error(\"Error when fetching $entityLowerCased\", e);"
echo "    return new ResponseEntity<$entityUpperCased>(HttpStatus.INTERNAL_SERVER_ERROR);"
echo "  }"
echo "  return new ResponseEntity<$entityUpperCased>(out${entityUpperCased}, HttpStatus.OK);"
echo "}"