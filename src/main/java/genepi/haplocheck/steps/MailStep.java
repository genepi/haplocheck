package genepi.haplocheck.steps;

import genepi.hadoop.common.WorkflowContext;
import genepi.hadoop.common.WorkflowStep;

public class MailStep extends WorkflowStep {

	@Override
	public boolean run(WorkflowContext context) {

		Object mail = context.getData("cloudgene.user.mail");
		Object name = context.getData("cloudgene.user.name");
		
		if (mail != null && !mail.equals("")) {

			context.ok("We have sent an email to <b>" + mail
					+ "</b>.");

			String subject = "Job " + context.getJobName() + " is complete.";
			String message = "Dear "
					+ name
					+ ",\nThe results can be downloaded from https://mitoverse.i-med.ac.at/start.html#!jobs/"
					+ context.getJobId() + "/results";

			context.incCounter("mode.registered", 1);
			context.submitCounter("mode.registered");

			try {
				return context.sendMail(subject, message);
			} catch (Exception e) {
				context.error("Sending mail failed: " + e.getMessage());
				return false;
			}

		} else {
			context.ok("No email notifications has been sent");
		}
		return true;
	}

}
