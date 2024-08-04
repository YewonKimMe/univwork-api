package net.univwork.api.api_v1.tool;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.entity.Workplace;
import net.univwork.api.api_v1.exception.ExcelFileAdditionException;
import net.univwork.api.api_v1.repository.jpa.JpaWorkplaceRepository;
import net.univwork.api.api_v1.service.UnivService;
import org.apache.poi.ss.usermodel.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequiredArgsConstructor
@Service
public class WorkplaceExcelFileParser {

    private final UnivService univService;

    private final JpaWorkplaceRepository jpaWorkplaceRepository;

    public void readExcel(MultipartFile file, Long univCode) {

        final String UNIV_NAME = univService.getUniv(univCode).getUnivName();

        if (!StringUtils.getFilenameExtension(file.getOriginalFilename()).equals("xlsx")) { // 확장자가 xlsx 이 아닌 경우
            throw new IllegalArgumentException("잘못된 파일 확장자 입니다.");
        }
        if (univService.countWorkplacePerUniv(univCode)> 0) { // 특정 대학교의 근로지가 존재할 경우
            throw new ExcelFileAdditionException("데이터베이스 내에 이미" + " '" + UNIV_NAME + "' " + ", 해당 대학교의 근로지가 존재하여 엑셀파일을 통한 일괄 추가 작업을 수행할 수 없습니다. 개별 추가를 이용해 주세요.");
        }
        try {
            InputStream inputStream = file.getInputStream(); // 입력 스트림 획득
            Workbook workbook = WorkbookFactory.create(inputStream); // 워크북 획득
            Sheet sheet = workbook.getSheetAt(0); // 시트 획득

            int numberOfRows = sheet.getPhysicalNumberOfRows(); // 행 갯수

            for (int r=0; r<numberOfRows; r++) { // 행 갯수-1 만큼 반복
                if (r == 0) { // 첫줄은 버림
                    continue;
                }
                Row row = sheet.getRow(r); // 행 획득
                int numberOfColumns = row.getPhysicalNumberOfCells();
                log.debug("columns={}", numberOfColumns);
                if (numberOfColumns != 11) {
                    throw new IllegalArgumentException("잘못된 양식의 엑셀 파일 입니다.");
                }
                Workplace workplace = new Workplace();
                for (int c=0; c<numberOfColumns; c++) {
                    Cell cell = row.getCell(c); // 셀 획득
                    String cellValue = cell.toString();

                    if (cell.getCellType() == CellType.NUMERIC) { // 숫자 형식일 경우 정수로 변환
                        cellValue = String.valueOf((int) cell.getNumericCellValue());
                    }
                    log.debug("cellValue={}",cellValue);
                    workplace.setUnivName(UNIV_NAME);
                    workplace.setUnivCode(univCode);
                    workplace.setViews(0L);
                    workplace.setCommentNum(0L);
                    switch (c) {
                        // 근로 유형
                        case 0 -> workplace.setWorkType(cellValue);

                        // 근로지 유형
                        case 1 -> workplace.setWorkplaceType(cellValue);

                        // 근로지명
                        case 2 -> workplace.setWorkplaceName(cellValue);

                        // 근로지 주소
                        case 3 -> workplace.setWorkplaceAddress(cellValue);

                        // 근로시간
                        case 4 -> workplace.setWorkTime(cellValue);

                        // 근로요일
                        case 5 -> workplace.setWorkDay(cellValue);

                        // 모집 인원
                        case 6 -> workplace.setRequiredNum(cellValue);

                        // 선호 학과
                        case 7 -> workplace.setPreferredDepartment(cellValue);

                        // 선호 학년
                        case 8 -> workplace.setPreferredGrade(cellValue);

                        // 상세직무내용
                        case 9 -> workplace.setJobDetail(cellValue);

                        // 비고
                        case 10 -> workplace.setNote(cellValue);

                        default -> throw new RuntimeException("올바르지 않은 셀 번호");
                    }
                    log.debug(workplace.toString());
                }
                // 저장
                jpaWorkplaceRepository.save(workplace);

            }
        } catch (FileNotFoundException e) {
            log.debug("File 을 찾을 수 없음");
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.debug("IO 예외");
            throw new RuntimeException(e);
        }
    }
}
