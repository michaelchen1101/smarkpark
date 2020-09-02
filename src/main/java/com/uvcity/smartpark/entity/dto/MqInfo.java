package com.uvcity.smartpark.entity.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author chenling
 * {
 *     "id": "123",
 *     "type":"event",
 *     "params": {
 *         "value": {
 *             "carNo": "粤B66688",
 *             "gate": "2",
 *             "parkNo": "1234567890",
 *             "parkingSpotNo": "440304000000XXX",
 *             "accessTime": "",
 *             "imagePath": "",
 *             "deviceNo": ""
 *         },
 *         "time": 1524448722000
 *     }
 * }
 */
@Data
public class MqInfo {

    private String id;
    private String type;
    private ParkInfo pInfo;
    private Long time;


}
