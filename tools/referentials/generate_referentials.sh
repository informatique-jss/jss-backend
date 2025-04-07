#!/bin/bash
set -e

read -p "Target module : " module
read -p "Uppercased (camel) new entity name : " entityUpperCased 
read -p "Plural uppercased (camel) new entity name : " entityPlural
read -p "Service name : " serviceName
read -p "Controller name lowercased : " controllerName
read -p "Entry point singular lowercased : " entryPointNameSingular
read -p "Entry point plural lowercased : " entryPointName
read -p "Generate referential ? (t / f) : " generateReferential

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

moveAndTransform backend/model/NewEntity.java ../../src/main/java/com/jss/osiris/modules/osiris/$module/model/$entityUpperCased.java
moveAndTransform backend/repository/NewEntityRepository.java ../../src/main/java/com/jss/osiris/modules/osiris/$module/repository/${entityUpperCased}Repository.java
moveAndTransform backend/service/NewEntityService.java ../../src/main/java/com/jss/osiris/modules/osiris/$module/service/${entityUpperCased}Service.java
moveAndTransform backend/service/NewEntityServiceImpl.java ../../src/main/java/com/jss/osiris/modules/osiris/$module/service/${entityUpperCased}ServiceImpl.java
moveAndTransform client/model/NewEntity.ts ../../client/src/app/modules/$module/model/${entityUpperCased}.ts
moveAndTransform client/services/newentity.service.ts ../../client/src/app/modules/$module/services/${serviceName}.service.ts

if [[ "$generateReferential" == "t" ]]
then
mkdir -p ../../client/src/app/modules/administration/components/referentials/referential-${entryPointNameSingular}
moveAndTransform client/referential/referential-new-entity.component.ts ../../client/src/app/modules/administration/components/referentials/referential-${entryPointNameSingular}/referential-${entryPointNameSingular}.component.ts																												  
fi

echo "@Autowired"
echo  "${entityUpperCased}Service  ${entityLowerCased}Service;"
echo "@GetMapping(inputEntryPoint + \"/$entryPointName\")"
echo "public ResponseEntity<List<$entityUpperCased>> get$entityPlural(){"
echo "	return new ResponseEntity<List<$entityUpperCased>>(${entityLowerCased}Service.get$entityPlural(), HttpStatus.OK);"
echo "	}"

echo ""
echo ""

echo "@PostMapping(inputEntryPoint + \"/$entryPointNameSingular\")"
echo "public ResponseEntity<$entityUpperCased> addOrUpdate$entityUpperCased("
echo "    @RequestBody $entityUpperCased $entityPluralLowerCased) throws OsirisValidationException, OsirisException {"
echo "    if ($entityPluralLowerCased.getId() != null)"
echo "      validationHelper.validateReferential($entityPluralLowerCased, true,\"$entityPluralLowerCased\");"
echo "    validationHelper.validateString($entityPluralLowerCased.getCode(), true,\"code\");"
echo "    validationHelper.validateString($entityPluralLowerCased.getLabel(), true,\"label\");"
echo " "
echo "  return new ResponseEntity<$entityUpperCased>(${entityLowerCased}Service.addOrUpdate$entityUpperCased($entityPluralLowerCased), HttpStatus.OK);"
echo "}"
 