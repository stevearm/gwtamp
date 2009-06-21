<?php
include 'includes/LocalConstants.php';

session_start();

if ($_REQUEST[LocalConstants::$PARAM_API_OPERATIONTYPE] == 'status') {
	printStatus();
} elseif ($_REQUEST[LocalConstants::$PARAM_API_OPERATIONTYPE] == 'login') {
	if ($_REQUEST['username'] == 'user' && $_REQUEST['password'] == 'pass') {
		$_SESSION[LocalConstants::$SESSION_USERNAME] = $_REQUEST['username'];
	}
	printStatus();
} elseif ($_REQUEST[LocalConstants::$PARAM_API_OPERATIONTYPE] == 'logout') {
	unset($_SESSION[LocalConstants::$SESSION_USERNAME]);
	printStatus();
} else {
	header("HTTP/1.1 403 Forbidden");
	printErrorResponse(403, "Unsupported HTTP method. Specify operation using '_operationType'");
}

function printStatus() {
	if (isset($_SESSION[LocalConstants::$SESSION_USERNAME])) {
		printGoodResponse('[{"loggedIn":true,"username":"'.$_SESSION[LocalConstants::$SESSION_USERNAME].'"}]');
	} else {
		printGoodResponse('[{"loggedIn":false}]');
	}
}

function printGoodResponse($dataString) {
	echo '{"response":{"status":0,"data":'.$dataString.'}}';
}

function printErrorResponse($errorCode, $errorString) {
	echo '{"response":{"status":'.$errorCode.',"message":'.$errorString.'}}';
}
?>