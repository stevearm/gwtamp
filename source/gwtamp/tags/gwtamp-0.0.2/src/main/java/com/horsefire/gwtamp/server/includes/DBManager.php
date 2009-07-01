<?php
/*
 * This class is needed to do anything with the database. On creation,  it connects
 * to the database, and all queries are routed through it. On  destruction, the database
 * connection is closed.
 */
class DBManager {
	private $dbHandle; // The connection to the database

	public function __construct() {
		$this->dbHandle = mysql_connect(ServerConstants::$DB_HOST, ServerConstants::$DB_USER, ServerConstants::$DB_PASS) or die('DBManager::__construct() cannot open connection to server');
		$dbSelected = mysql_select_db(ServerConstants::$DB_NAME, $this->dbHandle) or die('DBManager::__construct() cannot select proper database on server');
	}

	public function __destruct() {
		mysql_close($this->dbHandle);
	}

	/*
	 * Used to run a query on the database.
	 */
	function query($strQuery) {
		return mysql_query($strQuery, $this->dbHandle);
	}

	function scrubForDB($strQuery) {
		return mysql_real_escape_string($strQuery, $this->dbHandle);
	}

	function safeForDB($strQuery) {
		if ($strQuery == $this->ScrubForDB($strQuery, $this->dbHandle))
			return TRUE;
		else
			return FALSE;
	}

	function getLastID() {
		return mysql_insert_id($this->dbHandle);
	}
}
?>
