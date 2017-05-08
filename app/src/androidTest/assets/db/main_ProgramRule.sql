INSERT INTO ProgramRule (uid, code, name, displayName, created, lastUpdated, priority, condition, program, programStage) VALUES ('dahuKlP7jR2', null, 'Show error for high hemoglobin value', 'Show error for high hemoglobin value', '2016-04-12T15:58:54.797', '2016-04-12T15:58:54.798', null, '#{hemoglobin} > 99', 'lxAQ7Zs9VYR', null);
INSERT INTO ProgramRule (uid, code, name, displayName, created, lastUpdated, priority, condition, program, programStage) VALUES ('xOe5qCzRS0Y', null, 'Hide smoking cessation councelling', 'Hide smoking cessation councelling', '2016-04-12T15:55:02.043', '2016-04-12T15:55:02.044', null, '!#{womanSmoking} ', 'lxAQ7Zs9VYR', null);
INSERT INTO ProgramRule (uid, code, name, displayName, created, lastUpdated, priority, condition, program, programStage) VALUES ('GC4gpdoSD4r', null, 'Hemoglobin warning', 'Hemoglobin warning', '2016-04-12T15:57:18.356', '2016-04-12T15:57:18.357', null, '#{hemoglobin} < 9', 'lxAQ7Zs9VYR', null);
INSERT INTO ProgramRule (uid, code, name, displayName, created, lastUpdated, priority, condition, program, programStage) VALUES ('NAgjOfWMXg6', null, 'Ask for comment for low apgar', 'Ask for comment for low apgar', '2015-09-14T21:17:40.841', '2015-09-14T22:22:15.383', null, '#{apgarscore} >= 0 && #{apgarscore} < 4 && #{apgarcomment} == ''''', 'IpHINAT79UW', null);
INSERT INTO ProgramRule (uid, code, name, displayName, created, lastUpdated, priority, condition, program, programStage) VALUES ('tTPMkizzUZg', null, 'Demand comment if apgar is under zero', 'Demand comment if apgar is under zero', '2015-09-14T22:20:33.429', '2015-09-14T22:25:02.149', null, '#{apgarscore} <0 && #{apgarcomment} == ''''', 'IpHINAT79UW', null);
INSERT INTO ProgramRule (uid, code, name, displayName, created, lastUpdated, priority, condition, program, programStage) VALUES ('ppdTpuQC7Q5', null, 'Hide Apgar comment if score > 7', 'Hide Apgar comment if score > 7', '2015-08-07T18:41:55.082', '2015-09-14T21:25:08.933', null, '#{apgarscore} > 7', 'IpHINAT79UW', null);
INSERT INTO ProgramRule (uid, code, name, displayName, created, lastUpdated, priority, condition, program, programStage) VALUES ('fd3wL1quxGb', null, 'Hide pregnant if gender is male', 'Hide pregnant if gender is male', '2015-08-09T16:01:44.705', '2015-08-09T16:01:44.706', null, '#{gender} == ''Male''', 'eBAyeGv0exc', null);
INSERT INTO ProgramRule (uid, code, name, displayName, created, lastUpdated, priority, condition, program, programStage) VALUES ('tO1D62oB0tq', null, 'Hide smoking cessation if not smoking', 'Hide smoking cessation if not smoking', '2015-10-15T11:37:03.560', '2016-04-06T01:38:57.862', null, '!#{smoking}', 'WSGAb5XwJ3Y', null);
INSERT INTO ProgramRule (uid, code, name, displayName, created, lastUpdated, priority, condition, program, programStage) VALUES ('ruleonr1065', null, 'Hide fields to specify other medicine allergy if a', 'Hide fields to specify other medicine allergy if a', '2015-10-15T14:50:05.966', '2015-10-15T14:50:14.728', null, '#{othermedicineallergyexists}  !== ''Yes''', 'WSGAb5XwJ3Y', null);
INSERT INTO ProgramRule (uid, code, name, displayName, created, lastUpdated, priority, condition, program, programStage) VALUES ('jZ6TKNCRhdt', null, 'Check that address is on valid format', 'Check that address is on valid format', '2016-04-26T19:48:56.752', '2016-04-26T20:13:08.936', null, 'd2:hasValue(''streetAddress'') && !d2:validatePattern(A{streetAddress},''[\\w ]+ \\d+'')', 'WSGAb5XwJ3Y', null);
INSERT INTO ProgramRule (uid, code, name, displayName, created, lastUpdated, priority, condition, program, programStage) VALUES ('HTKIQDVMu0K', null, 'Hide field for treatment of aspirin', 'Hide field for treatment of aspirin', '2015-10-15T11:51:27.694', '2015-10-15T11:51:27.696', null, '(#{plurality} === ''Singleton''  || #{plurality} ===''Not assessed'' || #{plurality} === '''') && (#{diastolicbloodpressure} <= 90) &&  !#{renaldisease} && !#{chronichypertension}  && !#{autoimmunedisease} && !#{diabetes}', 'WSGAb5XwJ3Y', null);
INSERT INTO ProgramRule (uid, code, name, displayName, created, lastUpdated, priority, condition, program, programStage) VALUES ('xOm49QX4Nsc', null, 'Hide postpartum followup', 'Hide postpartum followup', '2017-01-20T13:29:16.163', '2017-01-20T13:29:16.163', null, '#{currentProgranancyOutcome}  != ''Live birth''', 'WSGAb5XwJ3Y', null);
INSERT INTO ProgramRule (uid, code, name, displayName, created, lastUpdated, priority, condition, program, programStage) VALUES ('ruleonr1066', null, 'Hide fields to specify other severe allergy if ans', 'Hide fields to specify other severe allergy if ans', '2015-10-15T14:50:12.216', '2015-10-15T14:50:14.597', null, '#{othersevereallergyexists}  !== ''Yes''', 'WSGAb5XwJ3Y', null);
INSERT INTO ProgramRule (uid, code, name, displayName, created, lastUpdated, priority, condition, program, programStage) VALUES ('ruleonr1055', null, 'Hide all fields for chronic conitions unless the w', 'Hide all fields for chronic conitions unless the w', '2015-10-15T14:49:10.503', '2015-10-15T14:49:15.518', null, '!#{chronicconditions}', 'WSGAb5XwJ3Y', null);
INSERT INTO ProgramRule (uid, code, name, displayName, created, lastUpdated, priority, condition, program, programStage) VALUES ('ruleonr1061', null, 'Hide field for -other chronic conditions specified', 'Hide field for -other chronic conditions specified', '2015-10-15T14:49:42.990', '2015-10-15T14:49:47.556', null, '!#{otherchronicconditionexists}', 'WSGAb5XwJ3Y', null);
INSERT INTO ProgramRule (uid, code, name, displayName, created, lastUpdated, priority, condition, program, programStage) VALUES ('RtCIjfyRB9L', null, 'Check that dates is within usual range in registering', 'Check that dates is within usual range in registering', '2015-10-15T15:12:57.494', '2015-10-15T15:47:36.034', null, '(d2:yearsBetween(A{born},V{current_date}) < 12) ||  (d2:yearsBetween(A{born},V{current_date}) > 50)', 'WSGAb5XwJ3Y', null);
INSERT INTO ProgramRule (uid, code, name, displayName, created, lastUpdated, priority, condition, program, programStage) VALUES ('hpO8g3CRAeC', null, 'Check that LMP date given is before event date', 'Check that LMP date given is before event date', '2015-10-15T15:47:11.224', '2015-10-15T16:02:16.286', null, 'd2:hasValue(''lmp'') && d2:daysBetween(#{lmp},V{event_date}) <= 0', 'WSGAb5XwJ3Y', null);
INSERT INTO ProgramRule (uid, code, name, displayName, created, lastUpdated, priority, condition, program, programStage) VALUES ('IdrDtQmRGrv', null, 'Check mobile number', 'Check mobile number', '2016-04-26T20:04:06.608', '2016-04-26T20:04:06.609', null, 'd2:validatePattern(A{mobile} ,''.*555.*'')', 'WSGAb5XwJ3Y', null);
INSERT INTO ProgramRule (uid, code, name, displayName, created, lastUpdated, priority, condition, program, programStage) VALUES ('NkpU28ZlfVh', null, 'Check that clinical estimate of due is aligned with LMP date', 'Check that clinical estimate of due is aligned with LMP date', '2015-10-15T15:38:07.796', '2015-10-15T16:19:25.381', null, 'd2:hasValue(''lmp'') && d2:hasValue(''duedateclinical'') && (d2:daysBetween(#{lmp},#{duedateclinical}) < 250) ', 'WSGAb5XwJ3Y', null);
INSERT INTO ProgramRule (uid, code, name, displayName, created, lastUpdated, priority, condition, program, programStage) VALUES ('ruleonr1062', null, 'Hide fields to specyfy allergies unless the woman ', 'Hide fields to specyfy allergies unless the woman ', '2015-10-15T14:49:49.616', '2015-10-15T14:49:50.963', null, '#{allergies} !== ''Yes''', 'WSGAb5XwJ3Y', null);