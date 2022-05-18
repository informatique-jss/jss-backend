#!/bin/bash
set -e

read -p "Target module : " module
read -p "Uppercased (camel) new entity name : " entityUpperCased 
read -p "Plural uppercased (camel) new entity name : " entityPlural
read -p "Service name : " serviceName
read -p "Controller name lowercased : " controllerName
read -p "Entry point lowercased : " entryPointName

#module=$1
#entityUpperCased=$2
entityLowerCased="$(tr "[:upper:]" "[:lower:]" <<< ${entityUpperCased:0:1})${entityUpperCased:1}"
#entityPlural=$3
entityPluralLowerCased="$(tr "[:upper:]" "[:lower:]" <<< ${entityPlural:0:1})${entityPlural:1}"
#serviceName=$4
#controllerName=$5
#entryPointName=$6

moveAndTransform() {
	sourceFile=$1
	targetFile=$2
	cp $sourceFile $targetFile
	sed -i "s/NewEntity/$entityUpperCased/g" $targetFile
	sed -i "s/newEntity/$entityLowerCased/g" $targetFile
	sed -i "s/NewEntities/$entityPlural/g" $targetFile
	sed -i "s/targetPackage/$module/g" $targetFile
	sed -i "s/controllerName/$controllerName/g" $targetFile
	sed -i "s/entryPointName/$entryPointName/g" $targetFile
	sed -i "s/entityPluralLowerCased/$entityPluralLowerCased/g" $targetFile
	
}

moveAndTransform backend/model/NewEntity.java ../../src/main/java/com/jss/jssbackend/modules/$module/model/$entityUpperCased.java
moveAndTransform backend/repository/NewEntityRepository.java ../../src/main/java/com/jss/jssbackend/modules/$module/repository/${entityUpperCased}Repository.java
moveAndTransform backend/service/NewEntityService.java ../../src/main/java/com/jss/jssbackend/modules/$module/service/${entityUpperCased}Service.java
moveAndTransform backend/service/NewEntityServiceImpl.java ../../src/main/java/com/jss/jssbackend/modules/$module/service/${entityUpperCased}ServiceImpl.java
moveAndTransform client/model/NewEntity.ts ../../client/src/app/modules/$module/model/${entityUpperCased}.ts
moveAndTransform client/services/newentity.service.ts ../../client/src/app/modules/$module/services/${serviceName}.service.ts

echo "@Autowired"
echo  "${entityUpperCased}Service  ${entityLowerCased}Service;"
echo "@GetMapping(inputEntryPoint + \"$entryPointName\")"
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