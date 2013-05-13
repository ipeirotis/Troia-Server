package com.datascience.gal;

import static org.junit.Assert.assertNotEquals;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.core.nominal.CategoryValue;
import com.datascience.core.nominal.NominalProject;
import com.datascience.core.nominal.decision.*;
import com.datascience.core.storages.MemoryJobStorage;
import com.datascience.utils.CostMatrix;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class BatchDawidSkeneEdgeCasesTest {

	NominalProject project;

	@Test
	public final void testWhenEachObjectHasOneLabel() {
                List<String> categories = Arrays.asList(new String[]{"yes", "no", "blank"});
		List<CategoryValue> priors = new ArrayList<CategoryValue>();
		for (String s : categories){
			priors.add(new CategoryValue(s, 1./3.));
		}

		MemoryJobStorage js = new MemoryJobStorage();
		project = new NominalProject(new BatchDawidSkene(), js.getNominalData("testid"), js.getNominalResults("testid", categories));
		project.initializeCategories(categories, null, null);

		Worker<String> w1 = new Worker<String>("A2G061ZJVQXGE");
		Worker<String> w2 = new Worker<String>("A224TK7J4KA1LV");
		Worker<String> w3 = new Worker<String>("A2ZUENR4ZLC3MN");
		project.getData().addAssign(new AssignedLabel<String>(w1, new LObject<String>("21K74J79KQW6KZ5ZI8OFKD455U4HJ6"), "blank"));
		project.getData().addAssign(new AssignedLabel<String>(w1, new LObject<String>("2FWPLSP75KLMPN1Y0O8QMQVJZMCZTJ"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("283PHIT3HVWAEB01E1AQA1727RZCKR"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2S3YHVI44OS23CC3P15Z0BAPDTU4Y4"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2NCXFCD45XCECEVNZ7BIV3TGXMSYWJ"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("295QQ7Q670519BUI3FTTYC1XUY7WC6"), "no"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("255NVRH1KHO9X796JKEVHLKWFFDZQ5"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("21Z1Q6SVQ8H8RD8PIZWOJVCI99NN4U"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2JGU98JHSTLBMCIANX0IWBJER94EQE"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2Z3KH1Q6SVQ80ZQH28XLDOBVK0S2LC"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2P3Z6R70G5R9R4QEF9574SSTVG2P9O"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("25UCF0CP5KXPC95DUBPGCPM7VGWWX9"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2AOYTWX4H3H282M8LN7IEIJRJNW4ZT"), "no"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2J5JQ2172Z9998O6WZ3VMBU1B1FZR6"), "no"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2SIHFL42J1LYOZT2ELD0QGFKPVS2NE"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2W36VCPWZ9RNWN0J7FJ70N3D1YKPA3"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2F8S1CSTMOFXA4AZUQSE6DNXRXCROW"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2TRKSSZVXX2QNWCPW4FYONIW95IN5Z"), "no"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2L13NAB6BFMFYFGTU7STRFDGMA2HU2"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2B3RGNTBJ3MMWONA1LE6AKHBH439VT"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("24ZMVHVKCJ01KBK2GXY9SQW69QD758"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2V99Y8TNKIMZBDNFRVJ2WM0POD59W3"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2FK4E6AUBXHWB8PAXN0VJZWDZ3NX1B"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2WAVGULF395SFX5BDTJ3VYNJJG5PBY"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2DMZMAZHHRRLYHIJZ1MRP1KHWR4CLU"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("27E618N46UXFV4M09Q5TVDSN128RPD"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2J9CJK63ZVE30IIF6W1QHJL5ZL59Y2"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2DS6X3YOCCF0VGNF93KIVIIX6YURQ4"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("23ZGYQ4J3NABP2XHRTFY6IT1115PC5"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2PFMB3Q39Z0BPLBJI7VE3STVZ26UJK"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("283FQ0ONNVRHKBZJLS7RJ76N3RBULM"), "no"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2F8S1CSTMOFXA4AZUQSE6DNXRXFORW"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("21UJ011K274JQ02L8KS8V46U5X5BD9"), "no"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2QAKMT6YTWX40UZX1PVDH9GIE0C0VB"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2C18EBDPGFL6E37RBNINLWIO3JLH2M"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2Y6VR14IXKO240KCQQNAEX3YWU59AV"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2P7JWKUDD8XMUU8YLDRBEUTOEBXMB2"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2525UFJ51Y7QXFHAA4J1KSTMWXQ9CX"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2NCXFCD45XCECEVNZ7BIV3TGXMQWYF"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("27ZM8JIA0RYN20KIVYD0DBMJ1C8507"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2I63W5XH0JPPB9KFASGL0P75S3FEKP"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("21RRJ85OZIZE0DS510FOY5EWXFB9XW"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2LAMEZZ2MIKM60C31NP3GTWW50TN6F"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("241KM05BMJTU0PEXS7G9ZA7SDEFEJQ"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2UZDM25L8I9NINOCQDXOM4QWFBYHRY"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2J1E1M7PKK9LANOJBO30CLZXX3YZSU"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2LWGDHDM25L8105U8K8E76OEC8MEO6"), "no"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2XANTL0XULRG6KTEF0DD55FPF5X2OT"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2FC98JHSTLB34RX6VN9OJJEJZWORFD"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2D2UDD8XMB3QM0HVNKLTW6T4MDIEPN"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2C73TVOK5UFJOSG22SFZNYQS9UI743"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("25XXRDS4IC1EH45SVTD19A2I3GUWZ6"), "no"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2PG1THV8ER9YRK5FU0QSU5KFPAT2PA"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2C79746OQ1SN9HPLILR59QKCV9RN39"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2YDKCJ011K27NAP4W4N698N4ECQ9BQ"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2XGQ4J3NAB6BYDXA0CPI11TJNV6ERF"), "yes"));//,F
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("23K5L8I9NZW605H10SVQ47T8ER6UKJ"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2ZCUU00OQKKAN90NK6TUA680MEQWWG"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2P1HSTLB3L0FUARD0PAERREVMNHUI8"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2581SNQQ7Q67JWJLWQER92TQKJN9TD"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2LISPW1INMHGHHMEF11BEBFMNXHH4I"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2SIHFL42J1LYOZT2ELD0QGFKPVRN2Y"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2ZRNZW6HEZ6OXV8RJ7Z6HGUMAJRZPE"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("214T6YTWX4H30T76GR09OI6IR91X2S"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2T5HMVHVKCJ0KS2XJIA7HKQWEJ1646"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2ALDHJHP4BDD37MTK5JXHX3GBNY4XW"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2IDP9746OQ1S6H822KY0D1QKK5I2ME"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2WIOHOVR14IX3FKGLG8EKWA65LO76O"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2GYKPEIB9BAW4J4S14WV5UD3UBHX08"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2WQ06UFBNFSVZL3AFNWS46NG9XLH3X"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2L13NAB6BFMFYFGTU7STRFDGMA2UHF"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2ALDHJHP4BDD37MTK5JXHX3GBN04XY"), "no"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2F4NCWYB49F9BJAC9FSJWNSTDZYOSK"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2BXBNFSVGULFM0NN8KEG9FS3VGE7LX"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("20JLY58B727MJ9YAWV41EYFS3XXU9S"), "no"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2FKAOO2GMMEG7FYL4QD6QAA20W877N"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("20JLY58B727MJ9YAWV41EYFS3XU9U4"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2A79RA7S5WM1P0GN0TY0RMHLI0JZUA"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w2, new LObject<String>("2AOYTWX4H3H282M8LN7IEIJRJNY4ZV"), "yes"));
		project.getData().addAssign(new AssignedLabel<String>(w3, new LObject<String>("2K5AB6BFMFFOHP0OD7AFLGESKIHJW8"), "yes"));

		project.getAlgorithm().compute();
		WorkerEstimator we = new WorkerEstimator(LabelProbabilityDistributionCostCalculators.get("ExpectedCost"));
		for (Double d : we.getCosts(project).values()){
			assertNotEquals(d, Double.NaN);
		}
	}

        @Test
        public final void testMultipleLabelsPerObject()
        {
                List<String> categories = Arrays.asList(new String[]{"blank","confirm_closed","confirm_no","confirm_nonrestaurant","confirm_yes"});
                CostMatrix<String> matrix = new CostMatrix<String>();
                matrix.add("blank", "blank", 5.0);
                matrix.add("blank", "confirm_closed",5.0);
                matrix.add("blank", "confirm_no", 5.0);
                matrix.add("blank", "confirm_nonrestaurant", 5.0);
                matrix.add("blank", "confirm_yes", 5.0);
                matrix.add("confirm_closed", "blank", 5.0);
                matrix.add("confirm_closed", "confirm_closed",0.0);
                matrix.add("confirm_closed", "confirm_no", 5.0);
                matrix.add("confirm_closed", "confirm_nonrestaurant", 5.0);
                matrix.add("confirm_closed", "confirm_yes", 5.0);
                matrix.add("confirm_no", "blank", 1.0);
                matrix.add("confirm_no", "confirm_closed", 1.0);
                matrix.add("confirm_no", "confirm_no", 0.0);
                matrix.add("confirm_no", "confirm_nonrestaurant", 1.0);
                matrix.add("confirm_no", "confirm_yes",1.0);
                matrix.add("confirm_nonrestaurant", "blank", 5.0);
                matrix.add("confirm_nonrestaurant", "confirm_closed", 5.0);
                matrix.add("confirm_nonrestaurant", "confirm_no", 5.0);
                matrix.add("confirm_nonrestaurant", "confirm_nonrestaurant", 0.0);
                matrix.add("confirm_nonrestaurant", "confirm_yes", 5.0);
                matrix.add("confirm_yes", "blank", 1.0);
                matrix.add("confirm_yes", "confirm_closed", 1.0);
                matrix.add("confirm_yes", "confirm_no", 1.0);
                matrix.add("confirm_yes", "confirm_nonrestaurant", 1.0);
                matrix.add("confirm_yes", "confirm_yes", 0.0);

		MemoryJobStorage js = new MemoryJobStorage();
                AbstractDawidSkene algorithm = new IncrementalDawidSkene();
                algorithm.setEpsilon(0.0001);
                algorithm.setIterations(10);
		project = new NominalProject(algorithm, js.getNominalData("testid"), js.getNominalResults("testid", categories));
		project.initializeCategories(categories, null, matrix);

                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A1R7CJMWXC79UO"), new LObject<String>("2OVZKPFE4EGD044XHZZIHNZWGWV7HC"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A2VRQML8Q3XYP4"), new LObject<String>("2OVZKPFE4EGD044XHZZIHNZWGWV7HC"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A3PRF4IIM2IQN2"), new LObject<String>("2OVZKPFE4EGD044XHZZIHNZWGWV7HC"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A3TXO6RKFIDFUV"), new LObject<String>("2Z3KH1Q6SVQ80ZQH28XLDOBVMXHL2G"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A3PRF4IIM2IQN2"), new LObject<String>("2Z3KH1Q6SVQ80ZQH28XLDOBVMXHL2G"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A31X3JCHS0BPFJ"), new LObject<String>("2Z3KH1Q6SVQ80ZQH28XLDOBVMXHL2G"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A1FQYUBCBNTQX7"), new LObject<String>("2V6ZFYQS1CST5FXS3RJ4QC1E8S3KN3"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A3PRF4IIM2IQN2"), new LObject<String>("2V6ZFYQS1CST5FXS3RJ4QC1E8S3KN3"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A31X3JCHS0BPFJ"), new LObject<String>("2V6ZFYQS1CST5FXS3RJ4QC1E8S3KN3"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A31X3JCHS0BPFJ"), new LObject<String>("2MCFJ51Y7QEOI6GL4F3S1MOF76TBEL"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A3PRF4IIM2IQN2"), new LObject<String>("2MCFJ51Y7QEOI6GL4F3S1MOF76TBEL"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A3TXO6RKFIDFUV"), new LObject<String>("2MCFJ51Y7QEOI6GL4F3S1MOF76TBEL"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A31X3JCHS0BPFJ"), new LObject<String>("2S4JTUHYW2GT8095J6WWU169874PK4"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A3PRF4IIM2IQN2"), new LObject<String>("2S4JTUHYW2GT8095J6WWU169874PK4"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("AH5ZIMHL7TNWC"), new LObject<String>("2S4JTUHYW2GT8095J6WWU169874PK4"), "confirm_no"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A3PRF4IIM2IQN2"), new LObject<String>("2S3YHVI44OS23CC3P15Z0BAPFQI4YO"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A31X3JCHS0BPFJ"), new LObject<String>("2S3YHVI44OS23CC3P15Z0BAPFQI4YO"), "confirm_closed"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("AH5ZIMHL7TNWC"), new LObject<String>("2S3YHVI44OS23CC3P15Z0BAPFQI4YO"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A31X3JCHS0BPFJ"), new LObject<String>("2KXDEY5COW0LZIHYKQIFTC6VX9G4VF"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A3PRF4IIM2IQN2"), new LObject<String>("2KXDEY5COW0LZIHYKQIFTC6VX9G4VF"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A2KET1HL1COET5"), new LObject<String>("2KXDEY5COW0LZIHYKQIFTC6VX9G4VF"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A31X3JCHS0BPFJ"), new LObject<String>("2FT49F9SSSHXKS1JZ6K5P5F8TJ4TX2"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("AH5ZIMHL7TNWC"), new LObject<String>("2FT49F9SSSHXKS1JZ6K5P5F8TJ4TX2"), "confirm_no"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A3TXO6RKFIDFUV"), new LObject<String>("2FT49F9SSSHXKS1JZ6K5P5F8TJ4TX2"), "confirm_no"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A3PRF4IIM2IQN2"), new LObject<String>("27G2RCJK63ZVXUZMCYLIIQ9JVK87WC"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A19BL9GZGXFWFT"), new LObject<String>("27G2RCJK63ZVXUZMCYLIIQ9JVK87WC"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("AH5ZIMHL7TNWC"), new LObject<String>("27G2RCJK63ZVXUZMCYLIIQ9JVK87WC"), "confirm_no"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("AH5ZIMHL7TNWC"), new LObject<String>("2JODG67D1X3VJ62VO2B2RE1MH40HAC"), "confirm_no"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A31X3JCHS0BPFJ"), new LObject<String>("2JODG67D1X3VJ62VO2B2RE1MH40HAC"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A2VRQML8Q3XYP4"), new LObject<String>("2JODG67D1X3VJ62VO2B2RE1MH40HAC"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A31X3JCHS0BPFJ"), new LObject<String>("26ZIT3HVWAVK1XKIV4T1F2Z9J5XEMD"), "confirm_closed"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A3TXO6RKFIDFUV"), new LObject<String>("26ZIT3HVWAVK1XKIV4T1F2Z9J5XEMD"), "confirm_closed"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("AH5ZIMHL7TNWC"), new LObject<String>("26ZIT3HVWAVK1XKIV4T1F2Z9J5XEMD"), "confirm_nonrestaurant"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A3PRF4IIM2IQN2"), new LObject<String>("20N1Y7QEOZFY9JJ747DONXRD2JYEH4"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A31X3JCHS0BPFJ"), new LObject<String>("20N1Y7QEOZFY9JJ747DONXRD2JYEH4"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A2KET1HL1COET5"), new LObject<String>("20N1Y7QEOZFY9JJ747DONXRD2JYEH4"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A3PRF4IIM2IQN2"), new LObject<String>("241KM05BMJTU0PEXS7G9ZA7SFB2EJ9"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A2L2ZXRHK3SNOP"), new LObject<String>("241KM05BMJTU0PEXS7G9ZA7SFB2EJ9"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("AH5ZIMHL7TNWC"), new LObject<String>("241KM05BMJTU0PEXS7G9ZA7SFB2EJ9"), "confirm_no"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A3PRF4IIM2IQN2"), new LObject<String>("2YDWAVKI62NJ9TJ2ED09YH6BU0SRJZ"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A19BL9GZGXFWFT"), new LObject<String>("2YDWAVKI62NJ9TJ2ED09YH6BU0SRJZ"), "confirm_closed"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("AH5ZIMHL7TNWC"), new LObject<String>("2YDWAVKI62NJ9TJ2ED09YH6BU0SRJZ"), "confirm_no"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A3PRF4IIM2IQN2"), new LObject<String>("2JV21O3W5XH02G7NUGBYMPLSZMLHB7"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A31X3JCHS0BPFJ"), new LObject<String>("2JV21O3W5XH02G7NUGBYMPLSZMLHB7"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A2L2ZXRHK3SNOP"), new LObject<String>("2JV21O3W5XH02G7NUGBYMPLSZMLHB7"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A3PRF4IIM2IQN2"), new LObject<String>("2JS1QP6AUC26W7O2PFO330FKAR5811"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A31X3JCHS0BPFJ"), new LObject<String>("2JS1QP6AUC26W7O2PFO330FKAR5811"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A1I3CXC17NIRWB"), new LObject<String>("2JS1QP6AUC26W7O2PFO330FKAR5811"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A3PRF4IIM2IQN2"), new LObject<String>("2CIFK0COK2JEKDPKWY0LZW6O9PSRKY"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A31X3JCHS0BPFJ"), new LObject<String>("2CIFK0COK2JEKDPKWY0LZW6O9PSRKY"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A2KET1HL1COET5"), new LObject<String>("2CIFK0COK2JEKDPKWY0LZW6O9PSRKY"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A3PRF4IIM2IQN2"), new LObject<String>("2IJK274J79KQFXJ3ZIXU5FCDEKDHFP"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("AH5ZIMHL7TNWC"), new LObject<String>("2IJK274J79KQFXJ3ZIXU5FCDEKDHFP"), "confirm_no"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A2L2ZXRHK3SNOP"), new LObject<String>("2IJK274J79KQFXJ3ZIXU5FCDEKDHFP"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A3TXO6RKFIDFUV"), new LObject<String>("234EGOOGQSCMP9S5E65I2UU0A36EEO"), "confirm_closed"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A3PRF4IIM2IQN2"), new LObject<String>("234EGOOGQSCMP9S5E65I2UU0A36EEO"), "confirm_closed"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A31X3JCHS0BPFJ"), new LObject<String>("234EGOOGQSCMP9S5E65I2UU0A36EEO"), "confirm_closed"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A3TXO6RKFIDFUV"), new LObject<String>("23QJIA0RYNJ9LE1FYEWBUJTURDD27P"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A3PRF4IIM2IQN2"), new LObject<String>("23QJIA0RYNJ9LE1FYEWBUJTURDD27P"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A31X3JCHS0BPFJ"), new LObject<String>("23QJIA0RYNJ9LE1FYEWBUJTURDD27P"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("AH5ZIMHL7TNWC"), new LObject<String>("2S5VP3TVOK5UYANWALHEWZFY07I52D"), "confirm_no"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A2L2ZXRHK3SNOP"), new LObject<String>("2S5VP3TVOK5UYANWALHEWZFY07I52D"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A3PRF4IIM2IQN2"), new LObject<String>("2S5VP3TVOK5UYANWALHEWZFY07I52D"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A31X3JCHS0BPFJ"), new LObject<String>("2ETAT2D5NQYNO6LLCX751YMNMBF84E"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("AH5ZIMHL7TNWC"), new LObject<String>("2ETAT2D5NQYNO6LLCX751YMNMBF84E"), "confirm_no"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A2L2ZXRHK3SNOP"), new LObject<String>("2ETAT2D5NQYNO6LLCX751YMNMBF84E"), "confirm_yes"));
             
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A3PRF4IIM2IQN2"), new LObject<String>("2Z2MN9U8P9Y3RKER9WUS2YIMAY9EVU"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("AH5ZIMHL7TNWC"), new LObject<String>("2Z2MN9U8P9Y3RKER9WUS2YIMAY9EVU"), "confirm_no"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A1I3CXC17NIRWB"), new LObject<String>("2Z2MN9U8P9Y3RKER9WUS2YIMAY9EVU"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("ABT7QTMIYXYO0"), new LObject<String>("2GXYQS1CSTMOYO984I9C9EYDXCZPM9"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A3PRF4IIM2IQN2"), new LObject<String>("2GXYQS1CSTMOYO984I9C9EYDXCZPM9"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("AH5ZIMHL7TNWC"), new LObject<String>("2GXYQS1CSTMOYO984I9C9EYDXCZPM9"), "confirm_no"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("AH5ZIMHL7TNWC"), new LObject<String>("2BLK4F0OHOVRKV0SW2TLH2HEMBR23K"), "blank"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A3PRF4IIM2IQN2"), new LObject<String>("2BLK4F0OHOVRKV0SW2TLH2HEMBR23K"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A2L2ZXRHK3SNOP"), new LObject<String>("2BLK4F0OHOVRKV0SW2TLH2HEMBR23K"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A3PRF4IIM2IQN2"), new LObject<String>("2X6OGQSCM6IATTA9U8LU80OQUZQHHN"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A2KET1HL1COET5"), new LObject<String>("2X6OGQSCM6IATTA9U8LU80OQUZQHHN"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A31X3JCHS0BPFJ"), new LObject<String>("2X6OGQSCM6IATTA9U8LU80OQUZQHHN"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A31X3JCHS0BPFJ"), new LObject<String>("29UWY2AOO2GM55YJ0UHSKM6IKPI442"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A3PRF4IIM2IQN2"), new LObject<String>("29UWY2AOO2GM55YJ0UHSKM6IKPI442"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("AH5ZIMHL7TNWC"), new LObject<String>("29UWY2AOO2GM55YJ0UHSKM6IKPI442"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A31X3JCHS0BPFJ"), new LObject<String>("2DJVP9746OQ1BE8LJ4X7851QUR3L1Z"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("ABT7QTMIYXYO0"), new LObject<String>("2DJVP9746OQ1BE8LJ4X7851QUR3L1Z"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A3PRF4IIM2IQN2"), new LObject<String>("2DJVP9746OQ1BE8LJ4X7851QUR3L1Z"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A31X3JCHS0BPFJ"), new LObject<String>("25XXRDS4IC1EH45SVTD19A2I5DKWZS"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A3PRF4IIM2IQN2"), new LObject<String>("25XXRDS4IC1EH45SVTD19A2I5DKWZS"), "confirm_no"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("AH5ZIMHL7TNWC"), new LObject<String>("25XXRDS4IC1EH45SVTD19A2I5DKWZS"), "confirm_no"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A2VRQML8Q3XYP4"), new LObject<String>("2DABRI8IUB2YD0QET6KLJ3L0PQZH56"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("AH5ZIMHL7TNWC"), new LObject<String>("2DABRI8IUB2YD0QET6KLJ3L0PQZH56"), "confirm_no"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A3PRF4IIM2IQN2"), new LObject<String>("2DABRI8IUB2YD0QET6KLJ3L0PQZH56"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A3PRF4IIM2IQN2"), new LObject<String>("2TOSK9RJ85OZ1QWCYO1PUOOQFTCU60"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("AH5ZIMHL7TNWC"), new LObject<String>("2TOSK9RJ85OZ1QWCYO1PUOOQFTCU60"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A2L2ZXRHK3SNOP"), new LObject<String>("2TOSK9RJ85OZ1QWCYO1PUOOQFTCU60"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A3PRF4IIM2IQN2"), new LObject<String>("2IBHV8ER9Y8T6B0HB6D5SFHSC334RM"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("ABT7QTMIYXYO0"), new LObject<String>("2IBHV8ER9Y8T6B0HB6D5SFHSC334RM"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("AH5ZIMHL7TNWC"), new LObject<String>("2IBHV8ER9Y8T6B0HB6D5SFHSC334RM"), "confirm_no"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A3TXO6RKFIDFUV"), new LObject<String>("294EZZ2MIKMNSLQKLCU81WWXSI97O0"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A19BL9GZGXFWFT"), new LObject<String>("294EZZ2MIKMNSLQKLCU81WWXSI97O0"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A1I3CXC17NIRWB"), new LObject<String>("294EZZ2MIKMNSLQKLCU81WWXSI97O0"), "confirm_yes"));
                project.getData().addAssign(new AssignedLabel<String>(new Worker<String>("A2VRQML8Q3XYP4"), new LObject<String>("2AOYTWX4H3H282M8LN7IEIJRLKM4ZF"), "confirm_yes"));

                project.getAlgorithm().compute();
		WorkerEstimator we = new WorkerEstimator(LabelProbabilityDistributionCostCalculators.get("ExpectedCost"));
		for (Double d : we.getCosts(project).values()){
			assertNotEquals(d, Double.NaN);
		}
        }
}
