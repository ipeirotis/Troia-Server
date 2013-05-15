package test.java.integration.algorithms.gal;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.core.nominal.NominalProject;
import com.datascience.core.nominal.decision.*;
import com.datascience.utils.CostMatrix;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertFalse;


public class GH88_DawidSkeneTest extends AbstractBase {

	@Override
	public List<String> getCategories() {
		return Arrays.asList(new String[]{"blank", "confirm_closed", "confirm_no", "confirm_nonrestaurant", "confirm_yes"});
	}

	@Override
	public void runTestScenario(NominalProject project) {
		List<String> categories = getCategories();
		CostMatrix<String> matrix = new CostMatrix<String>();
		matrix.add("blank", "blank", 5.0);
		matrix.add("blank", "confirm_closed", 5.0);
		matrix.add("blank", "confirm_no", 5.0);
		matrix.add("blank", "confirm_nonrestaurant", 5.0);
		matrix.add("blank", "confirm_yes", 5.0);
		matrix.add("confirm_closed", "blank", 5.0);
		matrix.add("confirm_closed", "confirm_closed", 0.0);
		matrix.add("confirm_closed", "confirm_no", 5.0);
		matrix.add("confirm_closed", "confirm_nonrestaurant", 5.0);
		matrix.add("confirm_closed", "confirm_yes", 5.0);
		matrix.add("confirm_no", "blank", 1.0);
		matrix.add("confirm_no", "confirm_closed", 1.0);
		matrix.add("confirm_no", "confirm_no", 0.0);
		matrix.add("confirm_no", "confirm_nonrestaurant", 1.0);
		matrix.add("confirm_no", "confirm_yes", 1.0);
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

		ensureComputed(project);
		WorkerEstimator we = new WorkerEstimator(LabelProbabilityDistributionCostCalculators.get("ExpectedCost"));
		for (Worker<String> worker : project.getData().getWorkers()) {
			System.out.println(we.getQuality(project, worker) + " :: " + we.getCost(project, worker) + " :: " + worker.getName());
			assertFalse(Double.isNaN(we.getQuality(project, worker)));
		}
	}
}
