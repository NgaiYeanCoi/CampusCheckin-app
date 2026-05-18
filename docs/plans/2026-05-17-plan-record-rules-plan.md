# 计划记录规则同步计划

## 当前背景

项目已经有 `AGENTS.md`、`docs/PRD.md`、`docs/schema.sql` 和部署运行文档，但原计划记录规则过宽，曾要求每次新对话、新 Agent 任务或较大开发任务都在 `docs/plans/` 下保存计划记录。用户进一步明确：只有涉及计划模式或正式实施计划时才需要记录。

## 目标

在仓库中明确 `docs/plans/` 的使用规则、命名规则和计划文件内容要求，同时避免普通问答、小修小补和直接执行型任务被强制记录。

## 计划改动

- 在 `AGENTS.md` 中修订 `Plan Record Rules`，将触发条件收窄为计划模式或正式实施计划。
- 更新 `docs/plans/README.md`，说明普通问答、简单解释、单步修复、环境检查不强制记录。
- 更新本次任务的计划记录，保留为后续计划文件示例。

## 涉及文件

- `AGENTS.md`
- `docs/plans/README.md`
- `docs/plans/2026-05-17-plan-record-rules-plan.md`

## 验证方式

- 检查 `AGENTS.md` 是否包含 `Plan Record Rules`。
- 检查 `docs/plans/` 目录是否存在。
- 检查计划文件命名是否符合 `YYYY-MM-DD-task-name-plan.md`。
- 检查旧的“每次新对话都必须创建计划”含义已被替换或限定。

## 当前状态

已完成。
