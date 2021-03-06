<?php

/*******************************************************************************
 * Copyright (c) 2011 Aalto University and the authors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * Authors:
 * XiaoHang Zou <zxhwd19871004@gmail.com>
 ******************************************************************************/


$con = mysql_connect("localhost","miyula","swufe80");
if (!$con){
    die('Could not connect: ' . mysql_error());
}
mysql_select_db("sideproject", $con);
$records_num = mysql_num_rows(mysql_query("SELECT * FROM file_upload_records"));
$sql = mysql_query("SELECT * FROM file_upload_records");
while ($file_upload_record = mysql_fetch_array($sql,MYSQL_ASSOC)) {
    if ($file_upload_record['status']=='uploaded') {
        $id = $file_upload_record['idrecord'];
        $fileName = $file_upload_record['file_name'];
        $mobileID = $file_upload_record['idmobile'];
        $personID = $file_upload_record['idperson'];
        $projectID = $file_upload_record['idproject'];
        transport_to_database($id,$fileName,$mobileID,$personID,$projectID);
    }
}

function transport_to_database($id,$fileName,$mobileID,$personID,$projectID) {
    mysql_query("UPDATE file_upload_records SET status = 'reading' WHERE idrecord = '$id'");
    $rSQLite = new PDO("sqlite:/var/www/html/side/sites/all/modules/custom/SIDEws/uploadFile/".$fileName);
    $sql = "SELECT * FROM observations";
    $result = $rSQLite->Query($sql);
    $result->setFetchMode(PDO::FETCH_ASSOC);
    foreach ($result as $each_result) {
        $time = $each_result['time'];
        $insert_observation_record_sql = "INSERT INTO observation_record (ido_type, idmobile, time, idperson,idproject) VALUES ('1','$mobileID','$time','$personID','$projectID')";
        mysql_query($insert_observation_record_sql);
        $ido_record = mysql_insert_id();
        foreach ($each_result as $key=>$value) {
            if ($key=="observation1") {
                $ido_keyname = get_id_keyname ("observation1");
                insert_record_value($ido_record,$ido_keyname,$value);
            }
            elseif ($key=="observation2") {
                $ido_keyname = get_id_keyname ("observation2");
                insert_record_value($ido_record,$ido_keyname,$value);
            }
            elseif ($key=="observation3") {
                $ido_keyname = get_id_keyname ("observation3");
                insert_record_value($ido_record,$ido_keyname,$value);
            }
        }
    }
    mysql_query("UPDATE file_upload_records SET status = 'recorded' WHERE idrecord = '$id'");
}

function get_id_keyname ($type) {
    $id = mysql_fetch_row(mysql_query("SELECT * FROM observation_keyname WHERE keyname='$type'"));
    return $id[0]; 
}

function insert_record_value($ido_record,$ido_keyname,$value) {
    mysql_query("INSERT INTO observation_record_value (ido_record,ido_keyname,value) VALUES ('$ido_record','$ido_keyname','$value')");
}
?>