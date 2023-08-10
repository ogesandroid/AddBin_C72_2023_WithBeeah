package com.gpd.addbin.bin.info;

import com.cocosw.favor.AllFavor;
import com.cocosw.favor.Default;

@AllFavor
public interface Account {


    @Default("No Database set")
    String getIsDatabaseSetForFirstTime();

    String setIsDatabaseSetForFirstTime(String isDatabaseSetForFirstTime);

    @Default("No Data Got")
    String getManualEntry();

    String setManualEntry(String setManualEntry);

    @Default("No Data Got")
    String getBinTime();

    String setBinTime(String binTime);

    @Default("No Data Got")
    String getDataFromServerFirstTime();

    String setDataFromServerFirstTime(String dataFromServerFirstTime);


    @Default("No Data Got")
    String getDataBinStatus();

    String setDataBinStatus(String binStatus);

    @Default("No Governorate")
    String getGovernorate();

    String setGovernorate(String governorate);

    @Default("No Governorate Id")
    String getGovernorateId();

    String setGovernorateId(String governorateId);

    @Default("No Governorate Value")
    String getGovernorateValue();

    String setGovernorateValue(String governorateValue);

    @Default("No Willayat")
    String getWillayat();

    String setWillayat(String willayat);


    @Default("No Willayat")
    String getWillayatId();

    String setWillayatId(String willayatId);

    @Default("No Willayat Value")
    String getWillayatValue();

    String setWillayatValue(String willayatValue);

    @Default("No Bin Capacity")
    String getCapacity();

    String setCapacity(String capacity);

    @Default("No Bin Capacity Id")
    String getCapacityId();

    String setCapacityId(String capacityId);

    @Default("No Capacity Value")
    String getCapacityValue();

    String setCapacityValue(String capacityValue);

    @Default("No Manufacturer")
    String getManufacturer();

    String setManufacturer(String manufacturer);

    @Default("No Manufacturer")
    String getManufacturerId();

    String setManufacturerId(String manufacturerId);

    @Default("")
    String getBeahCode();

    String setBeahCode(String beahCode);

    @Default("No RFID")
    String getRfid();

    String setRfid(String rfid);

    @Default("0")
    String getBinLatitude();

    String setBinLatitude(String binLatitude);

    @Default("0")
    String getBinLongitude();

    String setBinLongitude(String binLongitude);

    @Default("rfid")
    String getCodeofBinMethod();

    String setCodeofBinMethod(String binLongitude);

    @Default("")
    String getCallRfid();

    String setCallRfid(String callRfid);


    @Default("")
    String getCurrentScanTypeInPreview();

    String setCurrentScanTypeInPreview(String currentScanTypeInPreview);

    @Default("")
    String getCurrentTypeInPreview();

    String setCurrentTypeInPreview(String currentTypeInPreview);

    @Default("")
    String getCurrentTypeInPreviewValue();

    String setCurrentTypeInPreviewValue(String currentTypeInPreviewValue);

    @Default("")
    String getDontGoToWillayat();

    String setDontGoToWillayat(String dontGoToWillayat);

    @Default("")
    String getCompanyID();

    String setCompanyID(String companyID);

    @Default("")
    String getCompanyLogo();

    String setCompanyLogo(String companyLogo);

    @Default("0")
    String getCompanyFlag();

    String setCompanyFlag(String companyFlag);

    @Default("")
    String getChangeWillayathGovernarate();

    String setChangeWillayathGovernarate(String value);

    @Default("")
    String getGovernarateStatus();
    String setGovernarateStatus(String status);

    @Default("")
    String getWillayathStatus();
    String setWillayathStatus(String status);

    @Default("")
    String getGovernarateTitle();

    String setGovernarateTitle(String title);

    @Default("")
    String getWillayathTitle();

    String setWillayathTitle(String title);
}