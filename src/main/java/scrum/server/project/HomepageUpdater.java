package scrum.server.project;

import ilarkesto.base.Str;
import ilarkesto.base.Utl;
import ilarkesto.base.time.Date;
import ilarkesto.base.time.DateAndTime;
import ilarkesto.core.logging.Log;
import ilarkesto.integration.velocity.ContextBuilder;
import ilarkesto.integration.velocity.Velocity;
import ilarkesto.io.IO;
import ilarkesto.persistence.AEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import scrum.client.wiki.HtmlContext;
import scrum.client.wiki.WikiModel;
import scrum.client.wiki.WikiParser;
import scrum.server.admin.User;
import scrum.server.collaboration.Comment;
import scrum.server.collaboration.CommentDao;
import scrum.server.collaboration.Wikipage;
import scrum.server.common.BurndownChart;
import scrum.server.issues.Issue;
import scrum.server.pr.BlogEntry;
import scrum.server.release.Release;
import scrum.server.sprint.Sprint;

public class HomepageUpdater {

	private static Log log = Log.get(HomepageUpdater.class);

	private Project project;
	private MyHtmlContext htmlContext;
	private File templateDir;
	private File outputDir;
	private Velocity velocity;
	private Properties properties;

	public HomepageUpdater(Project project, String templatePath, String outputPath) {
		super();
		assert project != null;
		this.project = project;
		this.templateDir = new File(templatePath);
		this.outputDir = new File(outputPath);
		htmlContext = new MyHtmlContext(project);
		velocity = new Velocity(templateDir);

		loadProperties();
	}

	public HomepageUpdater(Project project) {
		this(project, project.getHomepageVelocityDir(), project.getHomepageDir());
	}

	public void processAll() {
		processDefaultTemplates();
		processIssueTemplates();
		processStoryTemplates();
		processReleaseTemplates();
		processBlogEntryTemplates();
		createSprintBurndownChart(700, 200);
	}

	private void processDefaultTemplates() {
		ContextBuilder context = new ContextBuilder();
		fillProject(context.putSubContext("project"));
		fillWiki(context.putSubContext("wiki"));
		fillBlog(context.putSubContext("blog"));
		fillSprintBacklog(context.putSubContext("sprintBacklog"));
		fillProductBacklog(context.putSubContext("productBacklog"));
		fillBugs(context);
		fillIdeas(context);
		fillClosedIssues(context);
		fillReleases(context);
		fillIssues(context);
		fillStories(context);

		File[] templateFiles = templateDir.listFiles();
		if (templateFiles == null) return;
		for (File templateFile : templateFiles) {
			String templateName = templateFile.getName();
			if (!templateName.endsWith(".vm")) continue;
			if (templateName.equals(Velocity.LIB_TEMPLATE_NAME)) continue;
			if (templateName.startsWith("iss.")) continue;
			if (templateName.startsWith("blg.")) continue;
			if (templateName.startsWith("sto.")) continue;
			String outputFileName = Str.removeSuffix(templateName, ".vm");
			velocity.processTemplate(templateName, new File(outputDir.getPath() + "/" + outputFileName), context);
		}
	}

	private void processEntityTemplate(ContextBuilder context, String reference) {
		fillProject(context.putSubContext("project"));
		fillProductBacklog(context.putSubContext("productBacklog"));
		fillSprintBacklog(context.putSubContext("sprintBacklog"));
		fillWiki(context.putSubContext("wiki"));

		String prefix = reference.substring(0, 3);
		File[] templateFiles = templateDir.listFiles();
		if (templateFiles == null) return;
		for (File templateFile : templateFiles) {
			String templateName = templateFile.getName();
			if (!templateName.endsWith(".vm")) continue;
			if (!templateName.startsWith(prefix + ".")) continue;
			String outputFileName = Str.removeSuffix(templateName, ".vm");
			outputFileName = Str.removePrefix(outputFileName, prefix + ".");
			outputFileName = reference + "." + outputFileName;
			velocity.processTemplate(templateName, new File(outputDir.getPath() + "/" + outputFileName), context);
		}
	}

	private void processReleaseTemplates() {
		List<Release> releases = new ArrayList<Release>(project.getReleases());
		Collections.sort(releases, Release.DATE_REVERSE_COMPARATOR);
		for (Release release : releases) {
			if (!release.isReleased()) continue;
			ContextBuilder context = new ContextBuilder();
			fillRelease(context.putSubContext("release"), release);
			String reference = release.getReference();
			processEntityTemplate(context, reference);
		}
	}

	public void processIssueTemplates() {
		List<Issue> issues = new ArrayList<Issue>(project.getPublishedIssues());
		for (Issue issue : issues) {
			processIssueTemplate(issue);
		}
	}

	public void processEntityTemplate(AEntity entity) {
		if (entity instanceof Issue) {
			processIssueTemplate((Issue) entity);
			return;
		}
		if (entity instanceof Requirement) {
			processStoryTemplate((Requirement) entity);
			return;
		}
		if (entity instanceof BlogEntry) {
			processBlogEntryTemplate((BlogEntry) entity);
			return;
		}
	}

	public void processIssueTemplate(Issue issue) {
		ContextBuilder context = new ContextBuilder();
		fillIssue(context.putSubContext("issue"), issue);
		processEntityTemplate(context, issue.getReference());
	}

	private void processStoryTemplates() {
		List<Requirement> requirements = new ArrayList<Requirement>(project.getRequirements());
		for (Requirement requirement : requirements) {
			if (requirement.isClosed()) continue;
			processStoryTemplate(requirement);
		}
	}

	private void processStoryTemplate(Requirement requirement) {
		ContextBuilder context = new ContextBuilder();
		fillStory(context.putSubContext("story"), requirement);
		processEntityTemplate(context, requirement.getReference());
	}

	private void processBlogEntryTemplates() {
		List<BlogEntry> entries = new ArrayList<BlogEntry>(project.getBlogEntrys());
		for (BlogEntry entry : entries) {
			if (!entry.isPublished()) continue;
			processBlogEntryTemplate(entry);
		}
	}

	private void processBlogEntryTemplate(BlogEntry entry) {
		ContextBuilder context = new ContextBuilder();
		fillBlogEntry(context.putSubContext("entry"), entry);
		processEntityTemplate(context, entry.getReference());
	}

	private void createSprintBurndownChart(int width, int height) {
		byte[] imageData = BurndownChart.createBurndownChartAsByteArray(project.getCurrentSprint(), width, height);
		IO.copyDataToFile(imageData,
			new File(outputDir.getPath() + "/sprint-burndown-" + width + "x" + height + ".png"));
	}

	private void fillIssues(ContextBuilder context) {
		List<Issue> issues = new ArrayList<Issue>(project.getBugsInCurrentRelease());
		Collections.sort(issues, Issue.ACCEPT_DATE_COMPARATOR);
		for (Issue issue : project.getIssues()) {
			if (!issue.isPublished()) continue;
			fillIssue(context.addSubContext("issues"), issue);
		}
	}

	private void fillBugs(ContextBuilder context) {
		List<Issue> issues = new ArrayList<Issue>(project.getBugsInCurrentRelease());
		Collections.sort(issues, Issue.ACCEPT_DATE_COMPARATOR);
		for (Issue issue : issues) {
			fillIssue(context.addSubContext("bugs"), issue);
		}
	}

	private void fillIdeas(ContextBuilder context) {
		List<Issue> issues = new ArrayList<Issue>(project.getOpenIdeas());
		Collections.sort(issues, Issue.ACCEPT_DATE_COMPARATOR);
		for (Issue issue : issues) {
			fillIssue(context.addSubContext("ideas"), issue);
		}
	}

	private void fillClosedIssues(ContextBuilder context) {
		List<Issue> issues = new ArrayList<Issue>(project.getClosedIssues());
		Collections.sort(issues, Issue.CLOSE_DATE_COMPARATOR);
		for (Issue issue : issues) {
			fillIssue(context.addSubContext("closedIssues"), issue);
		}
	}

	private void fillReleases(ContextBuilder context) {
		List<Release> releases = new ArrayList<Release>(project.getReleases());
		Collections.sort(releases, Release.DATE_REVERSE_COMPARATOR);
		for (Release release : releases) {
			if (!release.isReleased()) continue;
			fillRelease(context.addSubContext("releases"), release);
		}
	}

	private void fillIssue(ContextBuilder context, Issue issue) {
		context.put("id", issue.getId());
		context.put("reference", issue.getReference());
		context.put("label", toHtml(issue.getLabel()));
		context.put("description", wikiToHtml(issue.getDescription()));
		context.put("statement", wikiToHtml(issue.getStatement()));
		context.put("statusText", toHtml(issue.getStatusText()));
		if (issue.isOwnerSet()) context.put("owner", issue.getOwner().getName());
		if (issue.isFixed()) context.put("fixed", "true");
		fillComments(context, issue);
	}

	private void fillComments(ContextBuilder context, AEntity entity) {
		CommentDao commentDao = (CommentDao) entity.getDaoService().getDaoByClass(Comment.class);
		Collection<Comment> comments = commentDao.getPublishedCommentsByParent(entity);
		comments = Utl.sort(comments);
		for (Comment comment : comments) {
			fillComment(context.addSubContext("comments"), comment);
		}
	}

	private void fillComment(ContextBuilder context, Comment comment) {
		context.put("id", comment.getId());
		context.put("text", wikiToHtml(comment.getText()));
		context.put("author", toHtml(comment.getAuthorLabel()));
		User author = comment.getAuthor();
		if (author != null) {
			context.put("authorRoles", toHtml(project.getUsersRolesAsString(author, null, null)));
		}
		context.put("date", comment.getDateAndTime()
				.toString(DateAndTime.FORMAT_WEEKDAY_LONGMONTH_DAY_YEAR_HOUR_MINUTE));
	}

	private void fillRelease(ContextBuilder context, Release release) {
		context.put("id", release.getId());
		context.put("reference", release.getReference());
		context.put("label", toHtml(release.getLabel()));
		context.put("note", wikiToHtml(release.getNote()));
		context.put("releaseNotes", wikiToHtml(release.getReleaseNotes()));
		context.put("releaseDate", release.getReleaseDate());
		context.put("released", release.isReleased());
		context.put("major", release.isMajor());
		context.put("bugfix", release.isBugfix());
		fillComments(context, release);
	}

	private void fillBlog(ContextBuilder context) {
		List<BlogEntry> entries = new ArrayList<BlogEntry>(project.getBlogEntrys());
		Collections.sort(entries);
		for (BlogEntry entry : entries) {
			if (!entry.isPublished()) continue;
			fillBlogEntry(context.addSubContext("entries"), entry);
		}
	}

	private void fillBlogEntry(ContextBuilder context, BlogEntry entry) {
		context.put("id", entry.getId());
		context.put("reference", entry.getReference());
		context.put("url", entry.getUrl());
		context.put("urlEncodedForUrl", Str.encodeUrlParameter(entry.getUrl()));
		context.put("title", toHtml(entry.getTitle()));
		context.put("text", wikiToHtml(entry.getText()));
		context.put("textShort", wikiToHtml(Str.getFirstParagraph(entry.getText())));
		context.put("plainText", wikiToText(entry.getText()));
		DateAndTime date = entry.getDateAndTime();
		context.put("date", date.toString(Date.FORMAT_LONGMONTH_DAY_YEAR));
		context.put("rssDate", date.toString(DateAndTime.FORMAT_RFC822));
		fillComments(context, entry);
	}

	private void fillSprintBacklog(ContextBuilder context) {
		Sprint sprint = project.getCurrentSprint();
		context.put("label", toHtml(sprint.getLabel()));
		context.put("goal", wikiToHtml(sprint.getGoal()));
		context.put("begin", sprint.getBegin().toString(Date.FORMAT_LONGMONTH_DAY_YEAR));
		context.put("end", sprint.getEnd().toString(Date.FORMAT_LONGMONTH_DAY_YEAR));
		Release release = sprint.getRelease();
		if (release != null) context.put("release", release.getLabel());
		List<Requirement> requirements = new ArrayList<Requirement>(sprint.getRequirements());
		Collections.sort(requirements, project.getRequirementsOrderComparator());
		for (Requirement requirement : requirements) {
			fillStory(context.addSubContext("stories"), requirement);
		}
	}

	private void fillProductBacklog(ContextBuilder context) {
		List<Requirement> requirements = new ArrayList<Requirement>(project.getRequirements());
		Collections.sort(requirements, project.getRequirementsOrderComparator());
		for (Requirement requirement : requirements) {
			if (requirement.isClosed() || requirement.isInCurrentSprint()) continue;
			fillStory(context.addSubContext("stories"), requirement);
		}
	}

	private void fillStories(ContextBuilder context) {
		for (Requirement requirement : project.getRequirements()) {
			if (requirement.isClosed()) continue;
			fillStory(context.addSubContext("stories"), requirement);
		}
	}

	private void fillStory(ContextBuilder context, Requirement requirement) {
		context.put("id", requirement.getId());
		context.put("reference", requirement.getReference());
		context.put("label", toHtml(requirement.getLabel()));
		context.put("description", wikiToHtml(requirement.getDescription()));
		context.put("testDescription", wikiToHtml(requirement.getTestDescription()));
		if (requirement.isEstimatedWorkSet() && !requirement.isDirty())
			context.put("estimatedWork", requirement.getEstimatedWorkAsString());
		fillComments(context, requirement);
	}

	private void fillWiki(ContextBuilder context) {
		for (Wikipage page : project.getWikipages()) {
			context.put(page.getName(), wikiToHtml(page.getText()));
		}
	}

	private void fillProject(ContextBuilder context) {
		context.put("id", project.getId());
		context.put("label", toHtml(project.getLabel()));
		context.put("vision", project.getVision());
		context.put("shortDescription", toHtml(project.getShortDescription()));
		context.put("description", wikiToHtml(project.getDescription()));
		context.put("longDescription", wikiToHtml(project.getLongDescription()));
		Release currentRelease = project.getCurrentRelease();
		context.put("currentRelease", currentRelease == null ? "?" : toHtml(currentRelease.getLabel()));
	}

	public String wikiToHtml(String wikitext) {
		if (Str.isBlank(wikitext)) return null;
		WikiParser wikiParser = new WikiParser(wikitext);
		WikiModel model = wikiParser.parse(false);
		return model.toHtml(htmlContext);
	}

	public static String wikiToText(String wikitext) {
		if (Str.isBlank(wikitext)) return null;
		return wikitext;
	}

	public static String toHtml(String text) {
		if (text == null) return null;
		text = Str.toHtml(text);
		return text;
	}

	private void loadProperties() {
		File file = new File(outputDir.getPath() + "/kunagi.properties");
		if (!file.exists()) file = new File(templateDir.getPath() + "/kunagi.properties");
		if (!file.exists()) {
			properties = new Properties();
			return;
		}
		properties = IO.loadProperties(file, IO.UTF_8);
	}

	private static class MyHtmlContext implements HtmlContext {

		private Project project;

		public MyHtmlContext(Project project) {
			super();
			this.project = project;
		}

		@Override
		public String getEntityReferenceHrefOrOnclickAParameter(String reference) {
			return "href=\"" + reference + ".html\"";
		}

		@Override
		public String getDownloadUrlByReference(String reference) {
			return reference;
		}

		@Override
		public String getEntityLabelByReference(String reference) {
			AEntity entity = project.getEntityByReference(reference);
			return entity == null ? null : entity.toString();
		}

		@Override
		public boolean isAnchorLinks() {
			return true;
		}

	}

}
