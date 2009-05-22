<?php

session_start();

$dbManager = new DBManager();

if (!isset($_SESSION[LocalConstants::$SESSION_USERNAME])) {
	header("HTTP/1.1 401 Unauthorized");
	printErrorResponse(401, "You are not logged in");
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
		printErrorResponse(403, 'Unsupported HTTP method. Specify operation using "'.LocalConstants::$PARAM_API_OPERATIONTYPE.'"');
	}
}

function printGoodResponse($dataString) {
	echo '{"response":{"status":0,"data":'.$dataString.'}}';
}

function printErrorResponse($errorCode, $errorString) {
	echo '{"response":{"status":'.$errorCode.',"message":'.$errorString.'}}';
}

function renderRecord($recordArray) {
	$result = '{"id":'.$recordArray['id'].',"data":{';
	$firstItem = true;
	foreach (getStringDataFields() as $value) {
		if (isset($recordArray[$value])) {
			if ($firstItem) {
				$firstItem = false;
			} else {
				$result = $result.',';
			}
			$result = $result.'"'.$value.'":"'.$recordArray[$value].'"';
		}
	}
	foreach (getNumberDataFields() as $value) {
		if (isset($recordArray[$value])) {
			if ($firstItem) {
				$firstItem = false;
			} else {
				$result = $result.',';
			}
			$result = $result.'"'.$value.'":'.$recordArray[$value];
		}
	}
	$result = $result.'},"links":{';
	$firstItem = true;
	foreach (getLinkFields() as $value) {
		if ($firstItem) {
			$firstItem = false;
		} else {
			$result = $result.',';
		}
		$result = $result.'"'.$value.'":';
		if (isset($recordArray[$value])) {
			$result = $result.$recordArray[$value];
		} else {
			$result = $result.'-1';
		}
	}
	$result = $result.'}}';
	
	return $result;
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
	$firstRecord = true;
	$resultString = '[';
	while ($record = mysql_fetch_array($resRecords, MYSQL_ASSOC)) {
		if ($firstRecord) {
			$firstRecord = false;
		} else {
			$resultString = $resultString.',';
		}
		$resultString = $resultString.renderRecord($record);
	}
	$resultString = $resultString.']';
	printGoodResponse($resultString);
}

function updateRecord() {
	global $dbManager;
	if (isset($_REQUEST['id'])) {
		$strUpdate = 'UPDATE `'.getDatabaseName().'` SET ';
		$firstRecord = true;
		foreach (getStringDataFields() as $value) {
			if (isset($_REQUEST[$value])) {
				if ($firstRecord) {
					$firstRecord = false;
				} else {
					$strUpdate = $strUpdate.',';
				}
				$strUpdate = $strUpdate.'`'.$value.'`=\''.$dbManager->scrubForDB($_REQUEST[$value]).'\'';
			}
		}
		foreach (getNumberDataFields() as $value) {
			if (isset($_REQUEST[$value])) {
				if ($firstRecord) {
					$firstRecord = false;
				} else {
					$strUpdate = $strUpdate.',';
				}
				$strUpdate = $strUpdate.'`'.$value.'`='.$dbManager->scrubForDB($_REQUEST[$value]);
			}
		}
		foreach (getLinkFields() as $value) {
			if (isset($_REQUEST[$value])) {
				if ($firstRecord) {
					$firstRecord = false;
				} else {
					$strUpdate = $strUpdate.',';
				}
				$strUpdate = $strUpdate.'`'.$value.'`=';
				$scrubbedLinkValue = $dbManager->scrubForDB($_REQUEST[$value]);
				if ($scrubbedLinkValue == '-1') {
					$strUpdate = $strUpdate.'NULL';
				} else {
					$strUpdate = $strUpdate.$scrubbedLinkValue;
				}
			}
		}
		$strUpdate = $strUpdate.' WHERE `id`='.$dbManager->scrubForDB($_REQUEST['id']);
		$numberAffected = $dbManager->query($strUpdate);
		if ($numberAffected == 1) {
			listRecords();
		} else {
			header("HTTP/1.1 500 Internal Server Error");
			printErrorResponse(500, "Single record not updated: ".$numberAffected);
		}
	} else {
		header("HTTP/1.1 400 Bad Request");
		printErrorResponse(400, "'id' must be provided");
	}
}

function addRecord() {
	global $dbManager;
	$strQuery = 'INSERT INTO `'.getDatabaseName().'`(';
	$strValues = '';
	$firstRecord = true;
	foreach (getStringDataFields() as $value) {
		if (isset($_REQUEST[$value])) {
			if ($firstRecord) {
				$firstRecord = false;
			} else {
				$strQuery = $strQuery.',';
				$strValues = $strValues.',';
			}
			$strQuery = $strQuery.'`'.$value.'`';
			$strValues = $strValues.'\''.$dbManager->scrubForDB($_REQUEST[$value]).'\'';
		}
	}
	foreach (getNumberDataFields() as $value) {
		if (isset($_REQUEST[$value])) {
			if ($firstRecord) {
				$firstRecord = false;
			} else {
				$strQuery = $strQuery.',';
				$strValues = $strValues.',';
			}
			$strQuery = $strQuery.'`'.$value.'`';
			$strValues = $strValues.$dbManager->scrubForDB($_REQUEST[$value]);
		}
	}
	foreach (getLinkFields() as $value) {
		if (isset($_REQUEST[$value])) {
			if ($firstRecord) {
				$firstRecord = false;
			} else {
				$strQuery = $strQuery.',';
				$strValues = $strValues.',';
			}
			$strQuery = $strQuery.'`'.$value.'`';
			$scrubbedLinkValue = $dbManager->scrubForDB($_REQUEST[$value]);
			if ($scrubbedLinkValue == '-1') {
				$strValues = $strValues.'NULL';
			} else {
				$strValues = $strValues.$scrubbedLinkValue;
			}
		}
	}
	$strQuery = $strQuery.') VALUES ('.$strValues.')';
	$numberCreated = $dbManager->query($strQuery);
	if ($numberCreated == 1) {
		$_REQUEST['id'] = $dbManager->getLastId();
		listRecords();
	} else {
		header("HTTP/1.1 500 Internal Server Error");
		printErrorResponse(500, "Error creating record: ".$numberCreated);
	}
}


function removeRecord() {
	global $dbManager;
	if (isset($_REQUEST['id'])) {
		$strQuery = 'DELETE FROM `'.getDatabaseName().'` WHERE `id`='.$dbManager->scrubForDB($_REQUEST['id']);
		$numberDeleted = $dbManager->query($strQuery);
		if ($numberDeleted == 1) {
			printErrorResponse(200, "Record deleted");
		} else {
			header("HTTP/1.1 500 Internal Server Error");
			printErrorResponse(400, "Error deleting record: ".$numberDeleted);
		}
	} else {
		header("HTTP/1.1 400 Bad Request");
		printErrorResponse(400, "'id' must be provided");
	}
}

?>
