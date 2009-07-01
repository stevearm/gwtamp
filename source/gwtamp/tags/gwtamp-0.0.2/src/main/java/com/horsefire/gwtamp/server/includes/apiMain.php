<?php

session_start();

$dbManager = new DBManager();

if (!isset($_SESSION[LocalConstants::$SESSION_USERNAME])) {
	header("HTTP/1.1 401 Unauthorized");
	printRpcErrorResponse(401, "You are not logged in");
} else {
	if ($_REQUEST[LocalConstants::$PARAM_API_OPERATIONTYPE] == 'fetch') {
		listRecords();
	} elseif ($_REQUEST[LocalConstants::$PARAM_API_OPERATIONTYPE] == 'update') {
		updateRecord();
	} elseif ($_REQUEST[LocalConstants::$PARAM_API_OPERATIONTYPE] == 'add') {
		addRecord();
	} elseif ($_REQUEST[LocalConstants::$PARAM_API_OPERATIONTYPE] == 'remove') {
		removeRecord();
	} else {
		header("HTTP/1.1 403 Forbidden");
		printRpcErrorResponse(403, 'Unsupported HTTP method. Specify operation using "'.LocalConstants::$PARAM_API_OPERATIONTYPE.'"');
	}
}

function renderRecord($recordArray) {
	$dataArray = array();
	foreach (getStringDataFields() as $value) {
		$dataArray[] = '"'.$value.'":"'.$recordArray[$value].'"';
	}
	foreach (getNumberDataFields() as $value) {
		$dataArray[] = '"'.$value.'":'.$recordArray[$value];
	}
	
	$linkArray = array();
	foreach (getLinkFields() as $value) {
		if (isset($recordArray[$value])) {
			$linkArray[] = '"'.$value.'":'.$recordArray[$value];
		} else {
			$linkArray[] = '"'.$value.'":-1';
		}
	}
	
	return '{"id":'.$recordArray['id'].',"data":{'.implode(',', $dataArray).'},"links":{'.implode(',', $linkArray).'}}';
}

function listRecords() {
	global $dbManager;
	$strQuery = 'SELECT `id`';
	foreach (getStringDataFields() as $value) {
		$strQuery = $strQuery.', `'.$value.'`';
	}
	foreach (getNumberDataFields() as $value) {
		$strQuery = $strQuery.', `'.$value.'`';
	}
	foreach (getLinkFields() as $value) {
		$strQuery = $strQuery.', `'.$value.'`';
	}
	$strQuery = $strQuery.' FROM `'.getDatabaseName().'`';
	if (isset($_REQUEST['id'])) {
		$strQuery = $strQuery.' WHERE `id`=\''.$dbManager->scrubForDB($_REQUEST['id']).'\'';
	}
	$resRecords = $dbManager->query($strQuery);
	$results = array();
	while ($record = mysql_fetch_array($resRecords, MYSQL_ASSOC)) {
		$results[] = renderRecord($record);
	}
	printRpcGoodResponse($results);
}

function updateRecord() {
	global $dbManager;
	if (isset($_REQUEST['id'])) {
		$arrSetStatements = array();
		foreach (getStringDataFields() as $value) {
			if (isset($_REQUEST[$value])) {
				$arrSetStatements[] = '`'.$value.'`=\''.$dbManager->scrubForDB($_REQUEST[$value]).'\'';
			}
		}
		foreach (getNumberDataFields() as $value) {
			if (isset($_REQUEST[$value])) {
				$arrSetStatements[] = '`'.$value.'`='.$dbManager->scrubForDB($_REQUEST[$value]);
			}
		}
		foreach (getLinkFields() as $value) {
			if (isset($_REQUEST[$value])) {
				$scrubbedLinkValue = $dbManager->scrubForDB($_REQUEST[$value]);
				if ($scrubbedLinkValue == '-1') {
					$arrSetStatements[] = '`'.$value.'`=NULL';
				} else {
					$arrSetStatements[] = '`'.$value.'`='.$scrubbedLinkValue;
				}
			}
		}
		$strUpdate = 'UPDATE `'.getDatabaseName().'` SET '.implode(',', $arrSetStatements).' WHERE `id`='.$dbManager->scrubForDB($_REQUEST['id']);
		$numberAffected = $dbManager->query($strUpdate);
		if ($numberAffected == 1) {
			listRecords();
		} else {
			header("HTTP/1.1 500 Internal Server Error");
			printRpcErrorResponse(500, "Single record not updated: ".$numberAffected);
		}
	} else {
		header("HTTP/1.1 400 Bad Request");
		printRpcErrorResponse(400, "'id' must be provided");
	}
}

function addRecord() {
	global $dbManager;
	$arrNames = array();
	$arrValues = array();
	foreach (getStringDataFields() as $value) {
		if (isset($_REQUEST[$value])) {
			$arrNames[] = '`'.$value.'`';
			$arrValues[] = '\''.$dbManager->scrubForDB($_REQUEST[$value]).'\'';
		}
	}
	foreach (getNumberDataFields() as $value) {
		if (isset($_REQUEST[$value])) {
			$arrNames[] = '`'.$value.'`';
			$arrValues[] = $dbManager->scrubForDB($_REQUEST[$value]);
		}
	}
	foreach (getLinkFields() as $value) {
		if (isset($_REQUEST[$value])) {
			$arrNames[] = '`'.$value.'`';
			$scrubbedLinkValue = $dbManager->scrubForDB($_REQUEST[$value]);
			if ($scrubbedLinkValue == '-1') {
				$arrValues[] = 'NULL';
			} else {
				$arrValues[] = $scrubbedLinkValue;
			}
		}
	}
	$strQuery = 'INSERT INTO `'.getDatabaseName().'`('.implode(',', $arrNames).') VALUES ('.implode(',', $arrValues).')';
	$numberCreated = $dbManager->query($strQuery);
	if ($numberCreated == 1) {
		$_REQUEST['id'] = $dbManager->getLastId();
		listRecords();
	} else {
		header("HTTP/1.1 500 Internal Server Error");
		printRpcErrorResponse(500, "Error creating record: ".$numberCreated);
	}
}

function removeRecord() {
	global $dbManager;
	if (isset($_REQUEST['id'])) {
		$strQuery = 'DELETE FROM `'.getDatabaseName().'` WHERE `id`='.$dbManager->scrubForDB($_REQUEST['id']);
		$numberDeleted = $dbManager->query($strQuery);
		if ($numberDeleted == 1) {
			printRpcGoodResponse(array('"Record deleted"'));
		} else {
			header("HTTP/1.1 500 Internal Server Error");
			printRpcErrorResponse(500, "Error deleting record: ".$numberDeleted);
		}
	} else {
		header("HTTP/1.1 400 Bad Request");
		printRpcErrorResponse(400, "'id' must be provided");
	}
}
?>
