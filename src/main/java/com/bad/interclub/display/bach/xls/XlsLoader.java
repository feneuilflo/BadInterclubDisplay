package com.bad.interclub.display.bach.xls;


import com.bad.interclub.display.bach.model.*;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC;
import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING;

public final class XlsLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(XlsLoader.class);

    private static final String GENERAL_INFO_SHEET_NAME = "FM Senior";
    private static final String TEAM_COMPOSITION_SHEET_NAME = "Form4a";
    private static final String GUEST_SUFFIX = "V";
    private static final String HOST_SUFFIX = "H";


    private XlsLoader() {
    }

    public static Interclub loadFromFile(File file) throws IOException {
        POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(file));
        HSSFWorkbook wb = new HSSFWorkbook(fs);

        // load general informations
        Interclub.InterclubBuilder builder = initWithGeneralInformations(wb);

        // build host team
        Club host = buildClub(wb, true);

        // build guest team
        Club guest = buildClub(wb, false);

        // build matches
        extractMatches(wb, builder, host, guest);

        return builder.withFile(file.getAbsolutePath())
                .withHost(host).withGuest(guest)
                .build();
    }

    public static void updateWithFile(Interclub interclub, File file) throws IOException {
        POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(file));
        HSSFWorkbook wb = new HSSFWorkbook(fs);

        // build matches
        updateMatches(wb, interclub);
    }

    private static Interclub.InterclubBuilder initWithGeneralInformations(HSSFWorkbook wb) {
        LOGGER.info("Loading general informations...");
        HSSFSheet sheet = wb.getSheet(GENERAL_INFO_SHEET_NAME);
        FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();


        String division = sheet.getRow(3).getCell(6).getStringCellValue();
        LOGGER.info("division={}", division);

        int pool = (int) sheet.getRow(4).getCell(6).getNumericCellValue();
        LOGGER.info("pool={}", pool);

        String interclubNb = sheet.getRow(5).getCell(6).getStringCellValue();
        LOGGER.info("interclubNb={}", interclubNb);

        int number = (int) sheet.getRow(6).getCell(6).getNumericCellValue();
        LOGGER.info("number={}", number);

        int nbDaysSince1900 = (int) evaluator.evaluate(sheet.getRow(7).getCell(6)).getNumberValue();
        LocalDate date = LocalDate.of(1900, 1, 1).plusDays(nbDaysSince1900 - 2); // to be tested, maybe use LocalDate.now() unstead
        LOGGER.info("nbDaysSince1900={} - date={}", nbDaysSince1900, date);

        String location = sheet.getRow(8).getCell(6).getStringCellValue();
        LOGGER.info("location={}", location);

        // build
        return Interclub.newBuilder()
                .withDivision(division)
                .withPool(pool)
                .withInterclubNb(interclubNb)
                .withNumber(number)
                .withDate(null)
                .withLocation(location);
    }

    private static Club buildClub(HSSFWorkbook wb, boolean host) {
        HSSFSheet sheet = wb.getSheet(TEAM_COMPOSITION_SHEET_NAME + (host ? HOST_SUFFIX : GUEST_SUFFIX));
        String name = sheet.getRow(4).getCell(2).getStringCellValue();
        LOGGER.info("Loading players for team {}...", name);

        Club.ClubBuilder builder = Club.newBuilder()
                .withName(name);

        for (int rowIdx = 8; rowIdx < sheet.getLastRowNum(); rowIdx++) {
            HSSFRow row = sheet.getRow(rowIdx);
            if(row == null) {
                break;
            }

            int col = 1;

            // num licence
            HSSFCell cell = row.getCell(col++);
            if (cell == null || cell.getCellType() != CELL_TYPE_NUMERIC || cell.getNumericCellValue() == 0) {
                LOGGER.info("No more players...");
                break;
            }
            int licence = (int) cell.getNumericCellValue();

            // surname
            String surname = row.getCell(col++).getStringCellValue();

            // firstname
            String firstname = row.getCell(col++).getStringCellValue();

            // category
            String category = row.getCell(col++).getStringCellValue();

            // single
            String single_category = row.getCell(col++).getStringCellValue();
            int single_cpph = (int) row.getCell(col++).getNumericCellValue();

            // double
            String double_category = row.getCell(col++).getStringCellValue();
            int double_cpph = (int) row.getCell(col++).getNumericCellValue();

            // mixed double
            String mixed_double_category = row.getCell(col++).getStringCellValue();
            int mixed_double_cpph = (int) row.getCell(col++).getNumericCellValue();

            // build
            Player player = Player.newBuilder()
                    .withSurname(surname)
                    .withFirstname(firstname)
                    .withCategory(category)
                    .withLicence(licence)
                    .withSingleCategory(single_category)
                    .withSingleCPPH(single_cpph)
                    .withDoubleCategory(double_category)
                    .withDoubleCPPH(double_cpph)
                    .withMixedDoubleCategory(mixed_double_category)
                    .withMixedDoubleCPPH(mixed_double_cpph)
                    .build();
            LOGGER.info("created player {}", player);
            builder.withPlayer(player);
        }

        return builder.build();
    }

    private static void extractMatches(HSSFWorkbook wb, Interclub.InterclubBuilder builder, Club host, Club guest) {
        HSSFSheet sheet = wb.getSheet(GENERAL_INFO_SHEET_NAME);

        for (int rowIdx = 17; rowIdx < sheet.getLastRowNum(); rowIdx+=2) {
            HSSFRow row1 = sheet.getRow(rowIdx);
            HSSFRow row2 = sheet.getRow(rowIdx + 1);

            if(row1 == null) {
                LOGGER.info("No more matches...");
                break;
            }

            // check row
            HSSFCell cell = row1.getCell(6);
            if (cell == null || cell.getCellType() != CELL_TYPE_STRING
                    || Stream.of(Match.EMatchType.values()).noneMatch(type -> type.toString().equalsIgnoreCase(cell.getStringCellValue()))) {
                LOGGER.info("No more matches...");
                break;
            }

            Match.MatchBuilder matchBuilder = Match.newBuilder();

            // get match type
            String strMatchType = row1.getCell(6).getStringCellValue();
            Match.EMatchType matchType = Match.EMatchType.valueOf(strMatchType);
            matchBuilder.withType(matchType);

            // single or double ?
            boolean isDouble = strMatchType.startsWith("D");

            for(HSSFRow row : (isDouble ? Arrays.asList(row1, row2) : Collections.singletonList(row1))) {
                // get licence numbers
                int hostLicence = (int) row.getCell(3).getNumericCellValue();
                int guestLicence = (int) row.getCell(9).getNumericCellValue();

                // get players
                Player hostPlayer = host.getPlayers().stream().filter(player -> player.getLicence() == hostLicence).findAny()
                        .orElseThrow(() ->new IllegalStateException("Failed to find player in host team with licence " + hostLicence));
                Player guestPlayer = guest.getPlayers().stream().filter(player -> player.getLicence() == guestLicence).findAny()
                        .orElseThrow(() ->new IllegalStateException("Failed to find player in guest team with licence " + guestLicence));

                // add players
                matchBuilder.withHostPlayer(hostPlayer).withGuestPlayer(guestPlayer);
            }

            // build score
            MatchScore score = MatchScore.newBuilder()
                    .withSet1(getScore(row1, row2, 1))
                    .withSet2(getScore(row1, row2, 2))
                    .withSet3(getScore(row1, row2, 3))
                    .build();
            matchBuilder.withScore(score);

            // build match
            Match match = matchBuilder.build();
            LOGGER.info("created match {}", match);
            builder.withMatch(match);
        }

    }

    private static void updateMatches(HSSFWorkbook wb, Interclub interclub) {
        HSSFSheet sheet = wb.getSheet(GENERAL_INFO_SHEET_NAME);

        for (int rowIdx = 17; rowIdx < sheet.getLastRowNum(); rowIdx+=2) {
            HSSFRow row1 = sheet.getRow(rowIdx);
            HSSFRow row2 = sheet.getRow(rowIdx + 1);

            if(row1 == null) {
                LOGGER.info("No more matches...");
                break;
            }

            // check row
            HSSFCell cell = row1.getCell(6);
            if (cell == null || cell.getCellType() != CELL_TYPE_STRING
                    || Stream.of(Match.EMatchType.values()).noneMatch(type -> type.toString().equalsIgnoreCase(cell.getStringCellValue()))) {
                LOGGER.info("No more matches...");
                break;
            }

            // get match type
            String strMatchType = row1.getCell(6).getStringCellValue();
            Match.EMatchType matchType = Match.EMatchType.valueOf(strMatchType);
            Match match = interclub.getMatches().get(matchType);

            // update score
            updateScore(match.getScore().getSet1(), row1, row2, 1);
            updateScore(match.getScore().getSet2(), row1, row2, 2);
            updateScore(match.getScore().getSet3(), row1, row2, 3);
        }
    }

    private static SetScore getScore(HSSFRow row1, HSSFRow row2, int numSet) {
        SetScore setScore = SetScore.newBuilder()
                .withHostPoints(0)
                .withGuestPoints(0)
                .withGuestForfeit(false)
                .withHostForfeit(false)
                .build();
        updateScore(setScore, row1, row2, numSet);
        return setScore;
    }

    private static void updateScore(SetScore score, HSSFRow row1, HSSFRow row2, int numSet) {
        score.setHostPoints((int) row1.getCell(10 + 2 * numSet).getNumericCellValue());
        score.setGuestPoints((int) row2.getCell(11 + 2 * numSet).getNumericCellValue());
    }

}
