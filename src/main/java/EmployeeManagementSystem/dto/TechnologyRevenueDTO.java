package EmployeeManagementSystem.dto;

public class TechnologyRevenueDTO {
    private String name;
    private int projects;
    private double revenue;
    private double profit;
    private double loss;
    private double margin;

    public TechnologyRevenueDTO(String name, int projects, double revenue, double profit, double loss, double margin) {
        this.name = name;
        this.projects = projects;
        this.revenue = revenue;
        this.profit = profit;
        this.loss = loss;
        this.margin = margin;
    }

    // Public getters used by JSON serialization
    public String getName() { return name; }
    public int getProjects() { return projects; }
    public double getRevenue() { return revenue; }
    public double getProfit() { return profit; }
    public double getLoss() { return loss; }
    public double getMargin() { return margin; }
}