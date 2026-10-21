package EmployeeManagementSystem.service;
import java.util.*;
public final class HrLetterStarters {
 private HrLetterStarters(){}
 public static Map<String,String> all(){
  Map<String,String> result=new LinkedHashMap<>();
  String ref="Reference: {{DOCUMENT_REFERENCE}}\nIssue date: {{ISSUE_DATE}}\n\n";
  // Based on the six-page offer sample supplied by HR (18 March 2026).
  // Deliberately does NOT import the sample candidate, signature or example salary.
  // Every [COMPANY-APPROVED] review marker blocks template approval until HR has confirmed the wording.
  result.put("OFFER_LETTER", ref + """
# OFFER OF FULL-TIME EMPLOYMENT

To: {{NAME}}

Dear {{NAME}},

We are pleased to offer you full-time employment with {{COMPANY_NAME}} in the position of {{DESIGNATION}}, subject to the terms and conditions approved by the Company.

[COMPANY-APPROVED: confirm the exact employing legal entity, office/work location and offer wording. The reference scan uses different company names on the letterhead and closing block.]

# DATE OF APPOINTMENT

Your employment is proposed to commence on {{JOINING_DATE}}.

# PROBATION

The initial {{PROBATION_MONTHS}} month(s) of employment will be on probation. Confirmation and any extension of probation are subject to performance review and the Company's approved, communicated procedure.

[COMPANY-APPROVED: review probation confirmation, notice of extension and any probation-specific termination rights against signed current policy.]

# COMPENSATION

The proposed annual cost to company (CTC) is INR {{ANNUAL_CTC}}, as detailed in Appendix A. Statutory deductions and benefits are governed by the applicable approved salary structure and law.

[COMPANY-APPROVED: the reference describes ANNUAL FIXED SALARY, which is not necessarily the same as CTC. Confirm the approved remuneration basis and component treatment before issue.]

[[PAGE_BREAK]]
# COMPENSATION - CONTINUED

Compensation payments are subject to applicable withholding, tax and other statutory deductions. Individual tax filing remains the employee's responsibility where applicable.

Any recovery, offset or adjustment of payments must follow the approved employment terms and applicable law. The Company may review the salary structure according to its approved process.

[COMPANY-APPROVED: confirm the reference's salary restructuring, recovery and withholding provisions against current policy and applicable law; no statutory deduction rates are assumed here.]

# ANNUAL LEAVE ENTITLEMENT

Annual leave entitlement is determined by the current Company Leave Policy, subject to applicable statutory requirements.

# DUTIES

Your manager will provide your role description and responsibilities at commencement. Business needs may require travel, training, assignment to a different office, or temporary deputation in accordance with approved travel, reimbursement, mobility and employment policies.

[COMPANY-APPROVED: verify the geographic mobility, secondment, third-party assignment and expense-reimbursement language in the sample before issuing to a candidate.]

[[PAGE_BREAK]]
# TERMINATION OF EMPLOYMENT

Subject to the executed employment terms and applicable law, either party may terminate employment by giving notice. The currently entered offer notice field is {{NOTICE_DAYS}} day(s).

[COMPANY-APPROVED: the source PDF refers to THREE CALENDAR MONTHS' notice during and after probation; confirm whether the actual contractual term is calendar months or a stated number of days, and whether salary in lieu of notice is permissible.]

The reference sample also discusses termination without notice for serious misconduct or specified offences and the handling of final payments.

[COMPANY-APPROVED: insert vetted grounds, process, opportunity to respond, pay-in-lieu, settlement rules and applicable statutory safeguards. Do not issue this summary as the operative termination clause.]

# CONFIDENTIALITY

You are expected to protect non-public, proprietary and confidential information of the Company and its clients and to observe the current information-security policies and any separately executed confidentiality or employment agreements.

[COMPANY-APPROVED: confirm confidentiality duration, permitted disclosures, IP ownership, return of property and any disciplinary references against current signed agreements.]

[[PAGE_BREAK]]
# REFERENCE AND BACKGROUND CHECKS

The appointment is subject to lawful and proportionate verification of information provided during recruitment, any required reference checks and the eligibility conditions documented by HR.

[COMPANY-APPROVED: confirm the precise verification, discrepancy-review and employment-withdrawal procedure, including required candidate disclosures and privacy notices.]

# OFFER ACCEPTANCE AND VALIDITY

Please confirm acceptance by {{OFFER_EXPIRY}} using the approved HR acceptance method. The offer may lapse if not accepted by the stated deadline.

[COMPANY-APPROVED: the source scan describes a fifteen-day validity period. Verify that the individual OFFER_EXPIRY date corresponds to the authorized offer validity and issue date.]

# DOCUMENTS TO PRODUCE ON JOINING

The reference checklist includes recent photographs, the offer letter, educational certificates, prior employment and salary documents where applicable, identity/age evidence and a PAN card.

[COMPANY-APPROVED: replace this summary with the current necessary document checklist, secure upload instructions, privacy/retention notice and acceptable identity-verification alternatives. Do not collect Aadhaar or other sensitive information without a lawful, documented need.]

[[PAGE_BREAK]]
# ACCEPTANCE AND COMPANY AUTHORISATION

We welcome you to the team and look forward to your contribution. This offer is conditional on the verified terms, approved annexures and any documents expressly identified as part of the final employment agreement.

For and on behalf of {{COMPANY_NAME}}

Authorised signatory: {{SIGNATORY_NAME}}
Designation: {{SIGNATORY_DESIGNATION}}

[COMPANY-APPROVED: verify legal company identity, current signatory delegation and the precise acceptance instructions before approval. No signature is copied or applied by this template.]

CANDIDATE ACCEPTANCE

I, {{NAME}}, acknowledge receipt of this offer and confirm acceptance of its approved terms and Appendix A, subject to the agreed acceptance process.

Candidate signature (to be provided by candidate): ______________________
Date of acceptance: ______________________

[[PAGE_BREAK]]
# APPENDIX A - COMPENSATION AND BENEFITS

Proposed annual cost to company: INR {{ANNUAL_CTC}}

{{COMPENSATION_TABLE}}

The compensation table is populated only from the candidate's reviewed components. Monthly equivalents are shown for reference. Employer contributions are not take-home earnings, and deductions must be itemized separately where applicable.

[COMPANY-APPROVED: confirm actual components, annual fixed pay versus CTC, payment frequency, employer contributions, statutory deductions and any insurance provisions. The original sample figures are NOT copied into new offers.]

CANDIDATE ACKNOWLEDGEMENT OF ANNEXURE

Candidate name: {{NAME}}
Candidate signature (to be provided by candidate): ______________________
Date: ______________________
""".replace("[[PAGE_BREAK]]", "\f"));
  result.put("EMPLOYMENT_CERTIFICATE",ref+"# Employment certificate\n\nThis is to certify that {{NAME}} (employee ID {{EMPLOYEE_ID}}) is employed by {{COMPANY_NAME}} as {{DESIGNATION}}. The verified joining date is {{JOINING_DATE}}.\n\nThis certificate records the employment information available on the issue date. Verification requests may be sent to the contact shown on this letterhead.");
  result.put("EXPERIENCE_LETTER",ref+"# Experience letter\n\nThis is to certify that {{NAME}} (employee ID {{EMPLOYEE_ID}}) was employed by {{COMPANY_NAME}} from {{JOINING_DATE}} to {{LAST_WORKING_DATE}}. The designation recorded at separation was {{DESIGNATION}}.\n\nThe dates and role stated above have been checked against the employee and completed separation records. This letter does not add an unverified performance assessment.");
  result.put("RELIEVING_LETTER",ref+"# Relieving letter\n\nDear {{NAME}},\n\nThe verified employment record for {{EMPLOYEE_ID}} shows service with {{COMPANY_NAME}} as {{DESIGNATION}} from {{JOINING_DATE}} through {{LAST_WORKING_DATE}}.\n\nThe separation workflow is complete, including manager, HR, finance, asset and access clearance, recorded on {{CLEARANCE_DATE}}. You are relieved from the recorded role with effect from the last working date.\n\nThis letter is based on the completed clearance record; it is not a statement that a bank transfer or statutory settlement has occurred.");
  result.put("SALARY_CERTIFICATE",ref+"# Salary certificate\n\nThis is to certify that {{NAME}} (employee ID {{EMPLOYEE_ID}}) is employed as {{DESIGNATION}} by {{COMPANY_NAME}}.\n\nThe verified current salary structure records monthly gross earnings of INR {{MONTHLY_GROSS}} before applicable deductions.\n\n{{COMPENSATION_TABLE}}\n\nThis certificate states the current salary structure, not actual net pay or payment confirmation for a particular month.");
  result.put("ADDRESS_PROOF",ref+"# Employee address confirmation\n\nThe current HR record for {{NAME}} (employee ID {{EMPLOYEE_ID}}), employed by {{COMPANY_NAME}}, contains the following address:\n\n{{ADDRESS}}\n\nThis letter confirms the address recorded and checked by HR. It is not an independent statement of property ownership or residence entitlement.");
  result.put("NO_DUES_CERTIFICATE",ref+"# Clearance certificate\n\nThe separation checklist for {{NAME}} (employee ID {{EMPLOYEE_ID}}) has all required manager, HR, finance, asset and access clearance entries complete as of {{CLEARANCE_DATE}}.\n\nThis certificate confirms the checked clearance record for {{COMPANY_NAME}}. It does not independently certify payment of any unrecorded final settlement.");
  return Collections.unmodifiableMap(result);
 }
}
